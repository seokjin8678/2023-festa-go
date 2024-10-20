package com.festago.stage.domain

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class StageTest {

    @Test
    fun 공연이_축제_시작_일자_이전이면_예외() {
        // given
        val startTime = LocalDateTime.parse("2023-07-26T18:00:00")
        val ticketOpenTime = LocalDateTime.parse("2023-07-26T17:00:00")
        val festival = FestivalFixture.builder()
            .startDate(startTime.plusDays(1).toLocalDate())
            .endDate(startTime.plusDays(1).toLocalDate())
            .build()

        // when
        val ex = shouldThrow<BadRequestException> {
            Stage(
                startTime = startTime,
                ticketOpenTime = ticketOpenTime,
                festival = festival
            )
        }

        // then
        ex shouldHaveMessage ErrorCode.INVALID_STAGE_START_TIME.message
    }

    @Test
    fun 공연이_축제_종료_일자_이후이면_예외() {
        // given
        val startTime = LocalDateTime.parse("2023-07-26T18:00:00")
        val ticketOpenTime = LocalDateTime.parse("2023-07-26T17:00:00")
        val festival = FestivalFixture.builder()
            .startDate(startTime.minusDays(1).toLocalDate())
            .endDate(startTime.minusDays(1).toLocalDate())
            .build()

        // when
        val ex = shouldThrow<BadRequestException> {
            Stage(
                startTime = startTime,
                ticketOpenTime = ticketOpenTime,
                festival = festival
            )
        }

        // then
        ex shouldHaveMessage ErrorCode.INVALID_STAGE_START_TIME.message
    }

    @ParameterizedTest
    @ValueSource(strings = ["2023-07-26T18:00:00", "2023-07-26T18:00:01"])
    fun 티켓_오픈_시간이_무대_시작시간과_같거나_이후이면_예외(ticketOpenTime: LocalDateTime) {
        // given
        val startTime = LocalDateTime.parse("2023-07-26T18:00:00")
        val festival = FestivalFixture.builder()
            .startDate(startTime.toLocalDate())
            .endDate(startTime.toLocalDate())
            .build()

        // when
        val ex = shouldThrow<BadRequestException> {
            Stage(
                startTime = startTime,
                ticketOpenTime = ticketOpenTime,
                festival = festival
            )
        }

        ex shouldHaveMessage ErrorCode.INVALID_TICKET_OPEN_TIME.message
    }

    @Test
    fun 무대_생성() {
        // given
        val startTime = LocalDateTime.parse("2023-07-26T18:00:00")
        val ticketOpenTime = LocalDateTime.parse("2023-07-26T17:00:00")
        val festival = FestivalFixture.builder()
            .startDate(startTime.toLocalDate())
            .endDate(startTime.toLocalDate())
            .build()

        // when
        val actual = Stage(
            startTime = startTime,
            ticketOpenTime = ticketOpenTime,
            festival = festival
        )

        // then
        actual.startTime shouldBe startTime
        actual.ticketOpenTime shouldBe ticketOpenTime
        actual.festival shouldBe festival
    }

    @Nested
    inner class renewArtists {
        lateinit var stage: Stage

        @BeforeEach
        fun setUp() {
            stage = StageFixture.builder().id(1L).build()
        }

        @Test
        fun 아티스트를_추가할_수_있다() {
            // when
            stage.renewArtists(listOf(1, 2, 3))

            // then
            stage.artistIds shouldContainExactly listOf(1, 2, 3)
        }

        @Test
        fun 추가하려는_아티스트가_기존_아티스트에_없으면_기존_아티스트는_삭제된다() {
            // given
            stage.renewArtists(listOf(1))

            // when
            stage.renewArtists(listOf(2, 3))

            // then
            stage.artistIds shouldContainExactly listOf(2, 3)
        }
    }
}
