package com.festago.admin.application.integration

import com.festago.admin.application.AdminFestivalV1QueryService
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.querydsl.SearchCondition
import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalRepository
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRegion
import com.festago.school.domain.SchoolRepository
import com.festago.stage.domain.Stage
import com.festago.stage.domain.StageRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

internal class AdminFestivalV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminFestivalV1QueryService: AdminFestivalV1QueryService

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var stageRepository: StageRepository

    val now: LocalDate = LocalDate.parse("2077-06-30")
    val tomorrow: LocalDate = now.plusDays(1)

    lateinit var 테코대학교: School
    lateinit var 우테대학교: School
    lateinit var 테코대학교_축제: Festival
    lateinit var 테코대학교_공연_없는_축제: Festival
    lateinit var 우테대학교_축제: Festival
    lateinit var 테코대학교_공연: Stage
    lateinit var 우테대학교_첫째날_공연: Stage
    lateinit var 우테대학교_둘째날_공연: Stage

    @BeforeEach
    fun setUp() {
        val ticketOpenTime = now.atStartOfDay().minusWeeks(1)
        우테대학교 = schoolRepository.save(
            SchoolFixture.builder()
                .name("우테대학교")
                .region(SchoolRegion.서울)
                .build()
        )
        테코대학교 = schoolRepository.save(
            SchoolFixture.builder()
                .name("테코대학교")
                .region(SchoolRegion.서울)
                .build()
        )

        테코대학교_축제 = festivalRepository.save(
            FestivalFixture.builder()
                .name("테코대학교 축제")
                .startDate(now)
                .endDate(now)
                .school(테코대학교)
                .build()
        )
        테코대학교_공연_없는_축제 = festivalRepository.save(
            FestivalFixture.builder()
                .name("테코대학교 공연 없는 축제")
                .startDate(tomorrow)
                .endDate(tomorrow)
                .school(테코대학교)
                .build()
        )
        우테대학교_축제 = festivalRepository.save(
            FestivalFixture.builder()
                .name("우테대학교 축제")
                .startDate(now)
                .endDate(tomorrow)
                .school(우테대학교)
                .build()
        )

        테코대학교_공연 = stageRepository.save(
            StageFixture.builder()
                .startTime(now.atTime(18, 0))
                .ticketOpenTime(ticketOpenTime)
                .festival(테코대학교_축제)
                .build()
        )
        우테대학교_첫째날_공연 = stageRepository.save(
            StageFixture.builder()
                .startTime(now.atTime(18, 0))
                .ticketOpenTime(ticketOpenTime)
                .festival(우테대학교_축제)
                .build()
        )
        우테대학교_둘째날_공연 = stageRepository.save(
            StageFixture.builder()
                .startTime(tomorrow.atTime(18, 0))
                .ticketOpenTime(ticketOpenTime)
                .festival(우테대학교_축제)
                .build()
        )
    }

    @Nested
    inner class findAll {

        @Test
        fun 페이지네이션이_적용되어야_한다() {
            // given
            val pageable: Pageable = PageRequest.ofSize(2)
            val searchCondition = SearchCondition("", "", pageable)

            // when
            val response = adminFestivalV1QueryService.findAll(searchCondition)

            // then
            response.size shouldBe 2
            response.totalPages shouldBe 2
            response.totalElements shouldBe 3
        }

        @Test
        fun 공연의_수가_정확하게_반환되어야_한다() {
            // given
            val pageable: Pageable = PageRequest.ofSize(10)
            val searchCondition = SearchCondition("", "", pageable)

            // when
            val response = adminFestivalV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.stageCount } shouldContainExactly listOf(1, 0, 2)
        }

        @Nested
        inner class 정렬 {

            @Test
            fun 축제의_식별자로_정렬_되어야_한다() {
                // given
                val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"))
                val searchCondition = SearchCondition("", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(
                    우테대학교_축제.id, 테코대학교_공연_없는_축제.id, 테코대학교_축제.id
                )
            }

            @Test
            fun 축제의_이름으로_정렬이_되어야_한다() {
                // given
                val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
                val searchCondition = SearchCondition("", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.name } shouldContainExactly listOf(
                    우테대학교_축제.name, 테코대학교_공연_없는_축제.name, 테코대학교_축제.name
                )
            }

            @Test
            fun 학교의_이름으로_정렬이_되어야_한다() {
                // given
                val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "schoolName"))
                val searchCondition = SearchCondition("", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(
                    우테대학교_축제.id, 테코대학교_축제.id, 테코대학교_공연_없는_축제.id
                )
            }

            @Test
            fun 축제의_시작일으로_정렬이_되어야_한다() {
                // given
                val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "startDate"))
                val searchCondition = SearchCondition("", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(
                    테코대학교_축제.id, 우테대학교_축제.id, 테코대학교_공연_없는_축제.id
                )
            }

            @Test
            fun 축제의_종료일으로_정렬이_되어야_한다() {
                // given
                val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "endDate"))
                val searchCondition = SearchCondition("", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(
                    테코대학교_공연_없는_축제.id, 우테대학교_축제.id, 테코대학교_축제.id
                )
            }

            @Test
            fun 정렬_조건에_없으면_식별자의_오름차순으로_정렬이_되어야_한다() {
                // given
                val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "foo"))
                val searchCondition = SearchCondition("", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(
                    테코대학교_축제.id, 테코대학교_공연_없는_축제.id, 우테대학교_축제.id
                )
            }
        }

        @Nested
        inner class 검색 {

            @Test
            fun 축제의_식별자로_검색이_되어야_한다() {
                // given
                val pageable = Pageable.ofSize(10)
                val searchCondition = SearchCondition("id", 테코대학교_축제.id.toString(), pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(테코대학교_축제.id)
            }

            @Test
            fun 축제의_이름이_포함된_검색이_되어야_한다() {
                // given
                val pageable = Pageable.ofSize(10)
                val searchCondition = SearchCondition("name", "테코대학교", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(테코대학교_축제.id, 테코대학교_공연_없는_축제.id)
            }

            @Test
            fun 학교의_이름이_포함된_검색이_되어야_한다() {
                // given
                val pageable = Pageable.ofSize(10)
                val searchCondition = SearchCondition("schoolName", "우테대학교", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content.map { it.id } shouldContainExactly listOf(우테대학교_축제.id)
            }

            @Test
            fun 검색_필터가_비어있으면_필터링이_적용되지_않는다() {
                // given
                val pageable = Pageable.ofSize(10)
                val searchCondition = SearchCondition("", "글렌", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content shouldHaveSize 3
            }

            @Test
            fun 검색어가_비어있으면_필터링이_적용되지_않는다() {
                // given
                val pageable = Pageable.ofSize(10)
                val searchCondition = SearchCondition("id", "", pageable)

                // when
                val response = adminFestivalV1QueryService.findAll(searchCondition)

                // then
                response.content shouldHaveSize 3
            }
        }
    }

    @Nested
    inner class findDetail {

        @Test
        fun 축제의_식별자로_조회할_수_있어야_한다() {
            // when
            val actual = adminFestivalV1QueryService.findDetail(우테대학교_축제.identifier)

            // then
            actual.id shouldBe 우테대학교_축제.id
            actual.name shouldBe 우테대학교_축제.name
            actual.schoolId shouldBe 우테대학교.id
            actual.schoolName shouldBe 우테대학교.name
        }

        @Test
        fun 축제의_식별자에_해당하는_축제가_없으면_예외가_발생한다() {
            // when
            val ex = shouldThrow<NotFoundException> {
                adminFestivalV1QueryService.findDetail(4885L)
            }

            // then
            ex shouldHaveMessage ErrorCode.FESTIVAL_NOT_FOUND.message
        }
    }
}
