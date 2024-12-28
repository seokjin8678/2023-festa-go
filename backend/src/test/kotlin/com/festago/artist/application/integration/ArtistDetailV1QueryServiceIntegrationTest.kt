package com.festago.artist.application.integration

import com.festago.artist.application.ArtistDetailV1QueryService
import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalQueryInfoRepository
import com.festago.festival.domain.FestivalRepository
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRegion
import com.festago.school.domain.SchoolRepository
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaRepository
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.stage.domain.StageArtistRepository
import com.festago.stage.domain.StageRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.TimeInstantProvider
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.FestivalQueryInfoFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.SocialMediaFixture
import com.festago.support.fixture.StageArtistFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

internal class ArtistDetailV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var clock: Clock

    @Autowired
    lateinit var artistDetailV1QueryService: ArtistDetailV1QueryService

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var stageRepository: StageRepository

    @Autowired
    lateinit var stageArtistRepository: StageArtistRepository

    @Autowired
    lateinit var socialMediaRepository: SocialMediaRepository

    @Autowired
    lateinit var festivalQueryInfoRepository: FestivalQueryInfoRepository

    @Nested
    inner class 아티스트_상세_정보_조회 {
        @Test
        fun 조회할_수_있다() {
            // given
            val 아티스트_식별자 = createArtist("pooh").identifier
            makeSocialMedia(아티스트_식별자, OwnerType.ARTIST, SocialMediaType.INSTAGRAM)
            makeSocialMedia(아티스트_식별자, OwnerType.ARTIST, SocialMediaType.YOUTUBE)

            // when
            val actual = artistDetailV1QueryService.findArtistDetail(아티스트_식별자)

            // then
            actual.id shouldBe 아티스트_식별자
            actual.socialMedias shouldHaveSize 2
        }

        @Test
        fun 소셜_미디어가_없어도_조회할_수_있다() {
            // given
            val 아티스트_식별자 = createArtist("pooh").identifier

            // when
            val actual = artistDetailV1QueryService.findArtistDetail(아티스트_식별자)

            // then
            actual.id shouldBe 아티스트_식별자
            actual.socialMedias shouldHaveSize 0
        }

        @Test
        fun 소셜_미디어의_주인_아이디가_같더라도_주인의_타입에_따라_구분하여_조회한다() {
            // given
            val 아티스트_식별자 = createArtist("pooh").identifier
            makeSocialMedia(아티스트_식별자, OwnerType.ARTIST, SocialMediaType.INSTAGRAM)

            // when
            makeSocialMedia(아티스트_식별자, OwnerType.SCHOOL, SocialMediaType.YOUTUBE)
            val actual = artistDetailV1QueryService.findArtistDetail(아티스트_식별자)

            // then
            actual.socialMedias.map { it.type } shouldContainExactly listOf(SocialMediaType.INSTAGRAM)
        }

        @Test
        fun 존재하지_않는_아티스트를_검색하면_에외() {
            // when
            val ex = shouldThrow<NotFoundException> {
                artistDetailV1QueryService.findArtistDetail(4885L)
            }

            // then
            ex shouldHaveMessage ErrorCode.ARTIST_NOT_FOUND.message
        }

        fun makeSocialMedia(ownerId: Long, ownerType: OwnerType, socialMediaType: SocialMediaType): Long {
            val socialMedia = SocialMediaFixture.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .mediaType(socialMediaType)
                .build()
            return socialMediaRepository.save(socialMedia).identifier
        }
    }

    /**
     * 현재 시간은 6월 15일 18시 0분이다.
     *
     * 각 축제는 다음과 같이 진행 된다.
     *
     * 6월 14일~14일 서울대학교 축제
     *
     * 6월 15일~15일 부산대학교 축제
     *
     * 6월 16일~16일 대구대학교 축제
     *
     * 서울대학교 축제는 종료된 상태이다.
     *
     * 부산대학교 축제는 진행 중 상태이다.
     *
     * 대구대학교 축제는 진행 예정 상태이다.
     *
     * 아티스트A는 위 세 축제의 공연에 참여한 상태이다.
     */
    @Nested
    inner class 아티스트가_참여한_축제_목록_조회 {
        val now: LocalDateTime = LocalDateTime.parse("2077-06-15T18:00:00")
        val _6월_14일: LocalDate = LocalDate.parse("2077-06-14")
        val _6월_15일: LocalDate = LocalDate.parse("2077-06-15")
        val _6월_16일: LocalDate = LocalDate.parse("2077-06-16")

        lateinit var 아티스트A: Artist

        lateinit var 서울대학교_축제: Festival
        lateinit var 부산대학교_축제: Festival
        lateinit var 대구대학교_축제: Festival

        @BeforeEach
        fun setUp() {
            val 서울대학교 = createSchool("서울대학교", "seoul.ac.kr", SchoolRegion.서울)
            val 부산대학교 = createSchool("부산대학교", "busan.ac.kr", SchoolRegion.부산)
            val 대구대학교 = createSchool("대구대학교", "daegu.ac.kr", SchoolRegion.대구)

            서울대학교_축제 = createFestival("서울대학교 축제", _6월_14일, _6월_14일, 서울대학교)
            부산대학교_축제 = createFestival("부산대학교 축제", _6월_15일, _6월_15일, 부산대학교)
            대구대학교_축제 = createFestival("대구대학교 축제", _6월_16일, _6월_16일, 대구대학교)

            아티스트A = createArtist("아티스트A")

            createStage(서울대학교_축제, _6월_14일.atTime(18, 0), 아티스트A)
            createStage(부산대학교_축제, _6월_15일.atTime(18, 0), 아티스트A)
            createStage(대구대학교_축제, _6월_16일.atTime(18, 0), 아티스트A)

            given(clock.instant())
                .willReturn(TimeInstantProvider.from(now))
        }

        private fun createFestival(
            festivalName: String,
            startDate: LocalDate,
            endDate: LocalDate,
            school: School,
        ): Festival {
            val festival = festivalRepository.save(
                FestivalFixture.builder()
                    .name(festivalName)
                    .startDate(startDate)
                    .endDate(endDate)
                    .school(school)
                    .build()
            )
            festivalQueryInfoRepository.save(FestivalQueryInfoFixture.builder().festivalId(festival.identifier).build())
            return festival
        }

        private fun createStage(festival: Festival, startTime: LocalDateTime, vararg artists: Artist) {
            val stage = stageRepository.save(
                StageFixture.builder()
                    .festival(festival)
                    .startTime(startTime)
                    .build()
            )
            for (artist in artists) {
                stageArtistRepository.save(StageArtistFixture.builder(stage.identifier, artist.id).build())
            }
        }

        @Test
        fun 진행중인_축제_조회가_가능하다() {
            // given & when
            val actual = artistDetailV1QueryService.findArtistFestivals(
                아티스트A.identifier,
                null,
                null,
                false,
                PageRequest.ofSize(10)
            )

            // then
            actual.content.map { it.id } shouldContainExactly listOf(부산대학교_축제.id, 대구대학교_축제.id)
        }

        @Test
        fun 종료된_축제_조회가_가능하다() {
            // given & when
            val actual = artistDetailV1QueryService.findArtistFestivals(
                아티스트A.identifier,
                null,
                null,
                true,
                PageRequest.ofSize(10)
            )

            // then
            actual.content.map { it.id } shouldContainExactly listOf(서울대학교_축제.id)
        }

        @Test
        fun 커서_기반_페이징이_가능하다() {
            // given
            val firstResponse = artistDetailV1QueryService.findArtistFestivals(
                아티스트A.identifier,
                null,
                null,
                false,
                PageRequest.ofSize(1)
            )

            val firstFestivalResponse = firstResponse.content[0]

            // when
            val secondResponse = artistDetailV1QueryService.findArtistFestivals(
                아티스트A.identifier,
                firstFestivalResponse.id,
                firstFestivalResponse.startDate,
                false,
                PageRequest.ofSize(1)
            )

            // then
            secondResponse.content.map { it.id } shouldContainExactly listOf(대구대학교_축제.id)
        }
    }

    private fun createSchool(schoolName: String, domain: String, region: SchoolRegion): School {
        return schoolRepository.save(
            SchoolFixture.builder()
                .name(schoolName)
                .domain(domain)
                .region(region)
                .build()
        )
    }

    private fun createArtist(artistName: String): Artist {
        return artistRepository.save(
            ArtistFixture.builder()
                .name(artistName)
                .build()
        )
    }
}
