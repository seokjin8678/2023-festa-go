package com.festago.stage.application.command

import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.MemoryArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.exception.ValidException
import com.festago.festival.domain.Festival
import com.festago.festival.repository.FestivalRepository
import com.festago.festival.repository.MemoryFestivalRepository
import com.festago.stage.dto.command.StageCreateCommand
import com.festago.stage.repository.MemoryStageRepository
import com.festago.stage.repository.StageRepository
import com.festago.stage.repository.getOrThrow
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StageCreateServiceTest {

    lateinit var stageRepository: StageRepository

    lateinit var festivalRepository: FestivalRepository

    lateinit var artistRepository: ArtistRepository

    lateinit var stageCreateService: StageCreateService

    val festivalStartDate: LocalDate = LocalDate.parse("2077-06-30")
    val festivalEndDate: LocalDate = LocalDate.parse("2077-07-02")

    lateinit var 테코대학교_축제: Festival

    lateinit var 에픽하이: Artist
    lateinit var 소녀시대: Artist
    lateinit var 뉴진스: Artist

    @BeforeEach
    fun setUp() {
        stageRepository = MemoryStageRepository()
        festivalRepository = MemoryFestivalRepository()
        artistRepository = MemoryArtistRepository()
        stageCreateService = StageCreateService(
            stageRepository,
            festivalRepository,
            artistRepository,
            mockk(relaxed = true),
        )

        테코대학교_축제 = festivalRepository.save(
            FestivalFixture.builder()
                .name("테코대학교 축제")
                .startDate(festivalStartDate)
                .endDate(festivalEndDate)
                .build()
        )

        에픽하이 = artistRepository.save(ArtistFixture.builder().name("에픽하이").build())
        소녀시대 = artistRepository.save(ArtistFixture.builder().name("소녀시대").build())
        뉴진스 = artistRepository.save(ArtistFixture.builder().name("뉴진스").build())
    }

    @Nested
    internal inner class createStage {
        @Test
        fun ArtistIds에_중복이_있으면_예외() {
            // given
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제.identifier,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이.identifier, 에픽하이.identifier),
            )

            // when
            val ex = shouldThrow<ValidException> {
                stageCreateService.createStage(command)
            }

            // then
            ex shouldHaveMessage "artistIds에 중복된 값이 있습니다."
        }

        @Test
        fun ArtistIds의_개수가_10개를_초과하면_예외() {
            // given
            val artistIds = (1L..11L).toList()
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제.identifier,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = artistIds
            )

            // when
            val ex = shouldThrow<ValidException> {
                stageCreateService.createStage(command)
            }

            // then
            ex shouldHaveMessage "artistIds의 size는 10 이하여야 합니다."
        }

        @Test
        fun ArtistIds의_개수가_10개_이하이면_예외가_발생하지_않는다() {
            // given
            val artistIds = (1..10)
                .map { artistRepository.save(ArtistFixture.builder().build()) }
                .map { it.identifier }
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제.identifier,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = artistIds
            )

            // when & then
            shouldNotThrowAny { stageCreateService.createStage(command) }
        }

        @Test
        fun Festival_식별자에_대한_Festival이_없으면_예외() {
            // given
            val command = StageCreateCommand(
                festivalId = 4885L,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier)
            )

            // when
            val ex = shouldThrow<NotFoundException> {
                stageCreateService.createStage(command)
            }

            // then
            ex shouldHaveMessage ErrorCode.FESTIVAL_NOT_FOUND.message
        }

        @Test
        fun 아티스트_식별자_목록에_존재하지_않은_아티스트가_있으면_예외() {
            // given
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제.identifier,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier, 4885L)
            )

            // when
            val ex = shouldThrow<NotFoundException> {
                stageCreateService.createStage(command)
            }

            // then
            ex shouldHaveMessage ErrorCode.ARTIST_NOT_FOUND.message
        }

        @Test
        fun 성공하면_생성한_Stage에_대한_StageArtist가_저장된다() {
            // given
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제.identifier,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier)
            )

            // when
            val stageId = stageCreateService.createStage(command)

            // then
            val stage = stageRepository.getOrThrow(stageId)
            stage.artistIds shouldContainExactlyInAnyOrder listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier)
        }
    }
}
