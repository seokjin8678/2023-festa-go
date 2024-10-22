package com.festago.stage.application.command

import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.MemoryArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.exception.ValidException
import com.festago.festival.domain.Festival
import com.festago.stage.domain.Stage
import com.festago.stage.dto.command.StageUpdateCommand
import com.festago.stage.repository.MemoryStageRepository
import com.festago.stage.repository.StageRepository
import com.festago.stage.repository.getOrThrow
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StageUpdateServiceTest {
    lateinit var stageRepository: StageRepository
    lateinit var artistRepository: ArtistRepository
    lateinit var stageUpdateService: StageUpdateService

    var stageStartTime: LocalDateTime = LocalDateTime.parse("2077-06-30T18:00:00")
    var ticketOpenTime: LocalDateTime = stageStartTime.minusWeeks(1)
    lateinit var 테코대학교_축제: Festival
    lateinit var 테코대학교_축제_공연: Stage
    lateinit var 에픽하이: Artist
    lateinit var 소녀시대: Artist
    lateinit var 뉴진스: Artist

    @BeforeEach
    fun setUp() {
        stageRepository = MemoryStageRepository()
        artistRepository = MemoryArtistRepository()
        stageUpdateService = StageUpdateService(
            stageRepository,
            artistRepository,
            mockk(relaxed = true),
        )

        테코대학교_축제 = FestivalFixture.builder()
            .name("테코대학교 축제")
            .startDate(stageStartTime.toLocalDate())
            .endDate(stageStartTime.toLocalDate().plusDays(2))
            .build()
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

        테코대학교_축제_공연.renewArtists(listOf(에픽하이.identifier))
        테코대학교_축제_공연.renewArtists(listOf(소녀시대.identifier, 뉴진스.identifier))
    }

    @Nested
    internal inner class updateStage {
        @Test
        fun ArtistIds에_중복이_있으면_예외() {
            // given
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusHours(1),
                artistIds = listOf(에픽하이.identifier, 에픽하이.identifier)
            )

            // when
            val ex = shouldThrow<ValidException> {
                stageUpdateService.updateStage(테코대학교_축제_공연.identifier, command)
            }

            // then
            ex shouldHaveMessage "artistIds에 중복된 값이 있습니다."
        }

        @Test
        fun ArtistIds의_개수가_10개를_초과하면_예외() {
            // given
            val artistIds = (1L..11L).toList()
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusDays(1),
                artistIds = artistIds
            )

            // when
            val ex = shouldThrow<ValidException> {
                stageUpdateService.updateStage(테코대학교_축제_공연.identifier, command)
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
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusDays(1),
                artistIds = artistIds
            )

            // when & then
            shouldNotThrowAny { stageUpdateService.updateStage(테코대학교_축제_공연.identifier, command) }
        }

        @Test
        fun Stage에_대한_식별자가_없으면_예외() {
            // given
            val stageId = 4885L
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusDays(1),
                artistIds = listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier)
            )

            // when
            val ex = shouldThrow<NotFoundException> {
                stageUpdateService.updateStage(stageId, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.STAGE_NOT_FOUND.message
        }

        @Test
        fun 아티스트_식별자_목록에_존재하지_않은_아티스트가_있으면_예외() {
            // given
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusDays(1),
                artistIds = listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier, 4885L)
            )

            // when
            val ex = shouldThrow<NotFoundException> {
                stageUpdateService.updateStage(테코대학교_축제_공연.identifier, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.ARTIST_NOT_FOUND.message
        }

        @Test
        fun 성공하면_수정된_Stage에_반영된다() {
            // given
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusDays(1),
                artistIds = listOf(에픽하이.identifier, 소녀시대.identifier, 뉴진스.identifier)
            )

            // when
            stageUpdateService.updateStage(테코대학교_축제_공연.identifier, command)

            // then
            val actual = stageRepository.getOrThrow(테코대학교_축제_공연.identifier)
            actual.startTime shouldBe command.startTime
            actual.ticketOpenTime shouldBe command.ticketOpenTime
        }

        @Test
        fun 성공하면_수정된_Stage에_대한_StageArtist가_갱신된다() {
            // given
            val command = StageUpdateCommand(
                startTime = stageStartTime.minusHours(1),
                ticketOpenTime = ticketOpenTime.minusDays(1),
                artistIds = listOf(에픽하이.identifier)
            )

            // when
            stageUpdateService.updateStage(테코대학교_축제_공연.identifier, command)

            // then
            val stage = stageRepository.getOrThrow(테코대학교_축제_공연.identifier)
            stage.artistIds shouldContainExactly listOf(에픽하이.identifier)
        }
    }
}
