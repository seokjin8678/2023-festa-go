package com.festago.school.application.integration

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalQueryInfoRepository
import com.festago.festival.domain.FestivalRepository
import com.festago.school.application.v1.SchoolV1QueryService
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRepository
import com.festago.school.infrastructure.repository.query.v1.SchoolFestivalV1SearchCondition
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaRepository
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.FestivalQueryInfoFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.SocialMediaFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

internal class SchoolV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var schoolV1QueryService: SchoolV1QueryService

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var socialMediaRepository: SocialMediaRepository

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var festivalQueryInfoRepository: FestivalQueryInfoRepository

    @Nested
    inner class 학교_상세_정보_조회 {

        @Test
        fun 해당하는_학교가_존재하지_않으면_예외() {
            // when 
            val ex = shouldThrow<NotFoundException> {
                schoolV1QueryService.findDetailById(-1L)
            }

            // then
            ex shouldHaveMessage ErrorCode.SCHOOL_NOT_FOUND.message
        }

        @Test
        fun 학교에_소셜미디어가_존재하지_않아도_조회된다() {
            // given
            val school = schoolRepository.save(SchoolFixture.builder().build())

            // when
            val actual = schoolV1QueryService.findDetailById(school.identifier)

            // then
            actual.socialMedias shouldHaveSize 0
        }

        @Test
        fun 아티스트의_소셜미디어는_아이디가_같아도_조회되지_않는다() {
            // given
            val school = schoolRepository.save(SchoolFixture.builder().build())
            saveSocialMedia(school.identifier, OwnerType.SCHOOL, SocialMediaType.X)
            saveSocialMedia(school.identifier, OwnerType.ARTIST, SocialMediaType.YOUTUBE)

            // when
            val actual = schoolV1QueryService.findDetailById(school.identifier)

            // then
            actual.socialMedias shouldHaveSize 1
        }

        @Test
        fun 학교와_포함된_소셜미디어를_모두_조회한다() {
            // given
            val school = schoolRepository.save(SchoolFixture.builder().build())
            saveSocialMedia(school.identifier, OwnerType.SCHOOL, SocialMediaType.X)
            saveSocialMedia(school.identifier, OwnerType.SCHOOL, SocialMediaType.YOUTUBE)

            // when
            val actual = schoolV1QueryService.findDetailById(school.identifier)

            // then
            actual.socialMedias shouldHaveSize 2
        }

        private fun saveSocialMedia(ownerId: Long, ownerType: OwnerType, mediaType: SocialMediaType) {
            socialMediaRepository.save(
                SocialMediaFixture.builder()
                    .ownerId(ownerId)
                    .ownerType(ownerType)
                    .mediaType(mediaType)
                    .build()
            )
        }
    }

    @Nested
    inner class 학교별_축제_페이징_조회 {
        lateinit var school: School
        val today: LocalDate = LocalDate.now()

        @BeforeEach
        fun setUp() {
            school = schoolRepository.save(SchoolFixture.builder().build())
        }

        @Test
        fun 과거_축제만_가져온다() {
            // given
            // 진행중
            saveFestival(today, today.plusDays(1))
            saveFestival(today, today.plusDays(1))

            // 진행예정
            saveFestival(today.plusDays(1), today.plusDays(2))
            saveFestival(today.plusDays(1), today.plusDays(2))

            // 종료
            val lastFestival = saveFestival(today.minusDays(3), today.minusDays(1))

            val searchCondition = SchoolFestivalV1SearchCondition(null, null, true, Pageable.ofSize(10))

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition).content

            // then
            actual shouldHaveSize 1
            actual[0].id shouldBe lastFestival.id
        }

        @Test
        fun 현재_혹은_예정_축제만_가져온다() {
            // given
            // 진행 혹은 예정 축제
            saveFestival(today, today.plusDays(1))
            saveFestival(today.plusDays(1), today.plusDays(2))

            val searchCondition = SchoolFestivalV1SearchCondition(null, null, false, Pageable.ofSize(10))

            // 종료 축제
            saveFestival(today.minusDays(3), today.minusDays(1))

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition).content

            // then
            actual shouldHaveSize 2
        }

        @Test
        fun 현재_축제를_시작일자가_빠른순으로_조회한다() {
            // given
            saveFestival(today.plusDays(2), today.plusDays(3))
            saveFestival(today.plusDays(2), today.plusDays(3))
            val recentFestival = saveFestival(today, today.plusDays(1))
            val searchCondition = SchoolFestivalV1SearchCondition(null, null, false, Pageable.ofSize(10))

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition).content

            // then
            actual[0].id shouldBe recentFestival.id
        }

        @Test
        fun 과거_축제를_종료일자가_느린순으로_조회한다() {
            // given
            saveFestival(today.minusDays(4), today.minusDays(3))
            saveFestival(today.minusDays(3), today.minusDays(2))
            val recentFestival = saveFestival(today.minusDays(3), today.minusDays(1))
            val searchCondition = SchoolFestivalV1SearchCondition(null, null, true, Pageable.ofSize(10))

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition).content

            // then
            actual[0].id shouldBe recentFestival.id
        }

        @Test
        fun 페이징하여_현재_축제를_조회한다() {
            // given
            saveFestival(today, today.plusDays(3))
            val nextPageFirstReadFestival = saveFestival(today.plusDays(1), today.plusDays(1))
            val lastReadFestival = saveFestival(today, today.plusDays(1))
            saveFestival(today.plusDays(1), today.plusDays(1))
            saveFestival(today.plusDays(2), today.plusDays(2))
            val searchCondition = SchoolFestivalV1SearchCondition(
                lastReadFestival.id,
                lastReadFestival.startDate,
                false,
                Pageable.ofSize(2)
            )

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition).content

            // then
            actual shouldHaveSize 2
            actual[0].id shouldBe nextPageFirstReadFestival.id
        }

        @Test
        fun 페이징하여_과거_축제를_조회한다() {
            // given
            val yesterday = today.minusDays(1)

            saveFestival(yesterday.minusDays(2), yesterday)
            val nextPageFirstReadFestival = saveFestival(yesterday.minusDays(3), yesterday)
            val lastReadFestival = saveFestival(yesterday.minusDays(2), yesterday)
            saveFestival(yesterday.minusDays(4), yesterday)
            saveFestival(yesterday.minusDays(4), yesterday)
            val searchCondition = SchoolFestivalV1SearchCondition(
                lastReadFestival.id, lastReadFestival.startDate, true, Pageable.ofSize(2)
            )

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition).content

            // then
            actual shouldHaveSize 2
            actual[0].id shouldBe nextPageFirstReadFestival.id
        }

        @Test
        fun 다음_페이지가_존재한다() {
            // given
            saveFestival(today, today.plusDays(1))
            saveFestival(today, today.plusDays(1))
            val searchCondition = SchoolFestivalV1SearchCondition(null, null, false, Pageable.ofSize(1))

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition)

            // then
            actual.hasNext() shouldBe true
        }

        @Test
        fun 다음_페이지가_존재하지_않는다() {
            // given
            saveFestival(today, today.plusDays(1))
            val searchCondition = SchoolFestivalV1SearchCondition(null, null, false, Pageable.ofSize(1))

            // when
            val actual = schoolV1QueryService.findFestivalsBySchoolId(school.identifier, today, searchCondition)

            // then
            actual.hasNext() shouldBe false
        }

        private fun saveFestival(startDate: LocalDate, endDate: LocalDate): Festival {
            val festival = festivalRepository.save(
                FestivalFixture.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .school(school)
                    .build()
            )
            festivalQueryInfoRepository.save(FestivalQueryInfoFixture.builder().festivalId(festival.identifier).build())
            return festival
        }
    }
}
