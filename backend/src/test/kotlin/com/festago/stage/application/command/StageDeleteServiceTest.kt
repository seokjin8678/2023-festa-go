package com.festago.stage.application.command

import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.MemoryArtistRepository
import com.festago.festival.domain.Festival
import com.festago.festival.repository.FestivalRepository
import com.festago.festival.repository.MemoryFestivalRepository
import com.festago.stage.domain.Stage
import com.festago.stage.repository.MemoryStageRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StageDeleteServiceTest {

    lateinit var artistRepository: ArtistRepository
    lateinit var festivalRepository: FestivalRepository
    lateinit var stageRepository: StageRepository
    lateinit var stageDeleteService: StageDeleteService

    val stageStartTime: LocalDateTime = LocalDateTime.parse("2077-06-30T18:00:00")
    val ticketOpenTime: LocalDateTime = stageStartTime.minusWeeks(1)
    lateinit var 테코대학교_축제: Festival
    lateinit var 테코대학교_축제_공연: Stage
    lateinit var 에픽하이: Artist
    lateinit var 소녀시대: Artist
    lateinit var 뉴진스: Artist

    @BeforeEach
    fun setUp() {
        artistRepository = MemoryArtistRepository()
        festivalRepository = MemoryFestivalRepository()
        stageRepository = MemoryStageRepository()
        stageDeleteService = StageDeleteService(
            stageRepository,
            mockk(relaxed = true),
        )

        테코대학교_축제 = festivalRepository.save(
            FestivalFixture.builder()
                .name("테코대학교 축제")
                .startDate(stageStartTime.toLocalDate())
                .endDate(stageStartTime.toLocalDate().plusDays(2))
                .build()
        )
        테코대학교_축제_공연 = stageRepository.save(
            StageFixture.builder()
                .festival(테코대학교_축제)
                .startTime(stageStartTime)
                .ticketOpenTime(ticketOpenTime)
                .build()
        )
        에픽하이 = artistRepository.save(ArtistFixture.builder().name("에픽하이").build())
        소녀시대 = artistRepository.save(ArtistFixture.builder().name("소녀시대").build())
        뉴진스 = artistRepository.save(ArtistFixture.builder().name("뉴진스").build())
    }

    @Nested
    internal inner class deleteStage {
        @Test
        fun 삭제하려는_공연의_식별자가_존재하지_않아도_예외가_발생하지_않는다() {
            // given
            val stageId = 4885L

            // when
            shouldNotThrowAny { stageDeleteService.deleteStage(stageId) }
        }

        @Test
        fun 성공하면_저장된_Stage가_삭제된다() {
            // when
            stageDeleteService.deleteStage(테코대학교_축제_공연.identifier)

            // then
            stageRepository.findById(테코대학교_축제_공연.identifier) shouldBe null
        }
    }
}
