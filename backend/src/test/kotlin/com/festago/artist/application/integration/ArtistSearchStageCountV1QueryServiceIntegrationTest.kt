package com.festago.artist.application.integration

import com.festago.artist.application.ArtistSearchStageCountV1QueryService
import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistRepository
import com.festago.artist.dto.ArtistSearchStageCountV1Response
import com.festago.festival.domain.FestivalRepository
import com.festago.school.domain.SchoolRegion
import com.festago.school.domain.SchoolRepository
import com.festago.stage.domain.Stage
import com.festago.stage.domain.StageArtistRepository
import com.festago.stage.domain.StageRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.StageArtistFixture
import com.festago.support.fixture.StageFixture
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class ArtistSearchStageCountV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var artistSearchStageCountV1QueryService: ArtistSearchStageCountV1QueryService

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var stageRepository: StageRepository

    @Autowired
    lateinit var stageArtistRepository: StageArtistRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Nested
    inner class 검색 {
        lateinit var 아이유: Artist
        lateinit var 아이브: Artist
        lateinit var 아이들: Artist
        lateinit var _6월_15일_공연: Stage
        lateinit var _6월_16일_공연: Stage
        lateinit var _6월_17일_공연: Stage
        val _6월_15일: LocalDate = LocalDate.parse("2077-06-15")
        val _6월_16일: LocalDate = LocalDate.parse("2077-06-16")
        val _6월_17일: LocalDate = LocalDate.parse("2077-06-17")

        @BeforeEach
        fun setUp() {
            아이유 = artistRepository.save(ArtistFixture.builder().name("아이유").build())
            아이브 = artistRepository.save(ArtistFixture.builder().name("아이브").build())
            아이들 = artistRepository.save(ArtistFixture.builder().name("아이들").build())
            val school = schoolRepository.save(
                SchoolFixture.builder()
                    .domain("knu.ac.kr")
                    .name("경북대")
                    .region(SchoolRegion.대구)
                    .build()
            )
            val festival = festivalRepository.save(
                FestivalFixture.builder()
                    .name("축제")
                    .startDate(_6월_15일)
                    .endDate(_6월_17일)
                    .school(school)
                    .build()
            )

            _6월_15일_공연 = stageRepository.save(
                StageFixture.builder()
                    .startTime(_6월_15일.atStartOfDay())
                    .festival(festival)
                    .build()
            )
            _6월_16일_공연 = stageRepository.save(
                StageFixture.builder()
                    .startTime(_6월_16일.atStartOfDay())
                    .festival(festival)
                    .build()
            )
            _6월_17일_공연 = stageRepository.save(
                StageFixture.builder()
                    .startTime(_6월_17일.atStartOfDay())
                    .festival(festival)
                    .build()
            )
        }

        @Test
        fun 아티스트의_당일_및_예정_공연_갯수를_조회한다() {
            // given
            val today = _6월_16일.atStartOfDay()

            saveStageArtist(아이유, _6월_16일_공연)
            val 아이유_공연_갯수 = ArtistSearchStageCountV1Response(1, 0)

            saveStageArtist(아이브, _6월_16일_공연)
            saveStageArtist(아이브, _6월_17일_공연)
            val 아이브_공연_갯수 = ArtistSearchStageCountV1Response(1, 1)

            saveStageArtist(아이들, _6월_15일_공연)
            saveStageArtist(아이들, _6월_17일_공연)
            val 아이들_공연_갯수 = ArtistSearchStageCountV1Response(0, 1)

            // when
            val artistIds = listOf(아이브.identifier, 아이유.identifier, 아이들.identifier)
            val actual = artistSearchStageCountV1QueryService.findArtistsStageCountAfterDateTime(
                artistIds, today
            )

            actual[아이유.identifier] shouldBe 아이유_공연_갯수
            actual[아이브.identifier] shouldBe 아이브_공연_갯수
            actual[아이들.identifier] shouldBe 아이들_공연_갯수
        }

        @Test
        fun 아티스트가_오늘_이후_공연이_없으면_0개() {
            val today = _6월_16일.atStartOfDay()

            saveStageArtist(아이브, _6월_15일_공연)
            val 아이브_공연_갯수 = ArtistSearchStageCountV1Response(0, 0)

            // when
            val artistIds = listOf(아이브.identifier)
            val actual = artistSearchStageCountV1QueryService.findArtistsStageCountAfterDateTime(
                artistIds, today
            )

            // then
            actual[아이브.identifier] shouldBe 아이브_공연_갯수
        }
    }

    private fun saveStageArtist(artist: Artist, stage: Stage) {
        stageArtistRepository.save(StageArtistFixture.builder(stage.identifier, artist.id).build())
    }
}
