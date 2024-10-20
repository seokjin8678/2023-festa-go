package com.festago.admin.application.integration

import com.festago.admin.application.AdminStageV1QueryService
import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.domain.Festival
import com.festago.festival.repository.FestivalRepository
import com.festago.school.repository.SchoolRepository
import com.festago.stage.domain.Stage
import com.festago.stage.repository.StageArtistRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.StageArtistFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class AdminStageV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminStageV1QueryService: AdminStageV1QueryService

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var stageRepository: StageRepository

    @Autowired
    lateinit var stageArtistRepository: StageArtistRepository

    var _2077년_6월_15일: LocalDate = LocalDate.parse("2077-06-15")
    var _2077년_6월_16일: LocalDate = LocalDate.parse("2077-06-16")
    var _2077년_6월_17일: LocalDate = LocalDate.parse("2077-07-17")

    @Nested
    inner class findAllByFestivalId {

        @Test
        fun 존재하지_않는_축제의_식별자로_조회하면_빈_리스트가_반환된다() {
            // when
            val actual = adminStageV1QueryService.findAllByFestivalId(4885L)

            // then
            actual shouldHaveSize 0
        }

        @Nested
        inner class 축제에_공연이_없으면 {

            var 축제_식별자: Long = 0

            @BeforeEach
            fun setUp() {
                val 학교 = schoolRepository.save(SchoolFixture.builder().build())
                축제_식별자 = festivalRepository.save(
                    FestivalFixture.builder()
                        .startDate(_2077년_6월_15일)
                        .endDate(_2077년_6월_15일)
                        .school(학교)
                        .build()
                ).identifier
            }

            @Test
            fun 빈_리스트가_반환된다() {
                // when
                val actual = adminStageV1QueryService.findAllByFestivalId(축제_식별자)

                // then
                actual shouldHaveSize 0
            }
        }

        /**
         * 6월 15일 ~ 6월 17일까지 진행되는 축제
         *
         * 6월 15일 공연, 6월 16일 공연이 있다.
         *
         * 6월 15일 공연에는 아티스트A, 아티스트B가 참여한다.
         *
         * 6월 16일 공연에는 아티스트C가 참여한다.
         */
        @Nested
        internal inner class 축제에_공연이_있으면 {

            lateinit var 축제: Festival
            var 아티스트A_식별자: Long = 0
            var 아티스트B_식별자: Long = 0
            var 아티스트C_식별자: Long = 0
            var _6월_15일_공연_식별자: Long = 0
            var _6월_16일_공연_식별자: Long = 0

            @BeforeEach
            fun setUp() {
                아티스트A_식별자 = createArtist("아티스트A").id
                아티스트B_식별자 = createArtist("아티스트B").id
                아티스트C_식별자 = createArtist("아티스트C").id
                val 학교 = schoolRepository.save(SchoolFixture.builder().build())
                축제 = festivalRepository.save(
                    FestivalFixture.builder()
                        .startDate(_2077년_6월_15일)
                        .endDate(_2077년_6월_17일)
                        .school(학교)
                        .build()
                )
                _6월_15일_공연_식별자 = createStage(축제, _2077년_6월_15일, java.util.List.of(아티스트A_식별자, 아티스트B_식별자)).identifier
                _6월_16일_공연_식별자 = createStage(축제, _2077년_6월_16일, java.util.List.of(아티스트C_식별자)).identifier
            }

            @Test
            fun 공연의_시작_순서대로_정렬된다() {
                // when
                val actual = adminStageV1QueryService.findAllByFestivalId(축제.identifier)

                // then
                actual.map { it.id } shouldContainExactly listOf(_6월_15일_공연_식별자, _6월_16일_공연_식별자)
            }

            @Test
            fun 해당_일자의_공연에_참여하는_아티스트_목록을_조회할_수_있다() {
                // when
                val stageIdToArtists = adminStageV1QueryService.findAllByFestivalId(축제.identifier)
                    .associateBy({ it.id }, { it.artists })

                // then
                stageIdToArtists[_6월_15일_공연_식별자]!!.map { it.id } shouldContainExactlyInAnyOrder listOf(
                    아티스트A_식별자, 아티스트B_식별자
                )
                stageIdToArtists[_6월_16일_공연_식별자]!!.map { it.id } shouldContainExactlyInAnyOrder listOf(
                    아티스트C_식별자
                )
            }
        }
    }

    private fun createArtist(artistName: String): Artist {
        return artistRepository.save(
            ArtistFixture.builder()
                .name(artistName)
                .build()
        )
    }

    private fun createStage(festival: Festival, localDate: LocalDate, artistIds: List<Long>): Stage {
        val 공연 = stageRepository.save(
            StageFixture.builder()
                .festival(festival)
                .startTime(localDate.atTime(18, 0))
                .build()
        )
        for (artistId in artistIds) {
            stageArtistRepository.save(StageArtistFixture.builder(공연.id, artistId).build())
        }
        return 공연
    }

    @Nested
    inner class findById {

        @Nested
        inner class 식별자에_해당하는_공연이_없으면 {

            @Test
            fun 예외가_발생한다() {
                // when
                val ex = shouldThrow<NotFoundException> {
                    adminStageV1QueryService.findById(4885L)
                }

                // then
                ex shouldHaveMessage ErrorCode.STAGE_NOT_FOUND.message
            }
        }

        @Nested
        inner class 식별자에_해당하는_공연이_있으면 {

            lateinit var 아티스트B: Artist
            lateinit var 아티스트A: Artist
            lateinit var 아티스트C: Artist
            lateinit var 공연: Stage

            @BeforeEach
            fun setUp() {
                val 학교 = schoolRepository.save(SchoolFixture.builder().build())
                val 축제 = festivalRepository.save(
                    FestivalFixture.builder()
                        .startDate(_2077년_6월_15일)
                        .endDate(_2077년_6월_15일)
                        .school(학교)
                        .build()
                )
                아티스트A = createArtist("아티스트A")
                아티스트B = createArtist("아티스트B")
                아티스트C = createArtist("아티스트C")
                공연 = createStage(축제, _2077년_6월_15일, listOf(아티스트A.id, 아티스트B.id, 아티스트C.id))
            }

            @Test
            fun 공연의_정보가_정확하게_조회되어야_한다() {
                // when
                val actual = adminStageV1QueryService.findById(공연.identifier)

                // then
                actual.id shouldBe 공연.id
                actual.startDateTime shouldBe 공연.startTime
                actual.ticketOpenTime shouldBe 공연.ticketOpenTime
            }

            @Test
            fun 공연의_아티스트_목록이_조회되어야_한다() {
                // when
                val actual = adminStageV1QueryService.findById(공연.identifier)

                // then
                actual.artists.map { it.name } shouldContainExactlyInAnyOrder listOf(
                    아티스트A.name, 아티스트B.name, 아티스트C.name
                )
            }
        }
    }
}
