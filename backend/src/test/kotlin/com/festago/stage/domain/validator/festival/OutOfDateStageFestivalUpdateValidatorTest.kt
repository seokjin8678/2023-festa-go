package com.festago.stage.domain.validator.festival

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalDuration
import com.festago.stage.repository.MemoryStageRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.StageFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class OutOfDateStageFestivalUpdateValidatorTest {
    val festivalStartDate: LocalDate = LocalDate.parse("2077-02-19")
    val festivalEndDate: LocalDate = LocalDate.parse("2077-02-21")

    lateinit var stageRepository: StageRepository
    lateinit var validator: OutOfDateStageFestivalUpdateValidator
    lateinit var 축제: Festival

    @BeforeEach
    fun setUp() {
        stageRepository = MemoryStageRepository()
        validator = OutOfDateStageFestivalUpdateValidator(stageRepository)
        축제 = FestivalFixture.builder()
            .id(4885L)
            .startDate(festivalStartDate)
            .endDate(festivalEndDate)
            .build()
    }

    @Nested
    inner class 축제에_등록된_공연이_있을때 {

        @BeforeEach
        fun setUp() {
            val ticketOpenTime = festivalStartDate.atStartOfDay().minusWeeks(1)
            // 19, 20, 21 일자의 공연 생성
            for (i in 0..2) {
                stageRepository.save(
                    StageFixture.builder()
                        .festival(축제)
                        .ticketOpenTime(ticketOpenTime)
                        .startTime(festivalStartDate.plusDays(i.toLong()).atTime(18, 0))
                        .build()
                )
            }
        }

        @Test
        fun 축제의_일자를_확장하면_예외가_발생하지_않는다() {
            // given
            val startDate = festivalStartDate.minusDays(1)
            val endDate = festivalEndDate.plusDays(1)

            축제.changeFestivalDuration(FestivalDuration(startDate, endDate))

            // when & then
            shouldNotThrowAny { validator.validate(축제) }
        }

        @Test
        fun 축제의_시작일자를_축소하면_예외가_발생한다() {
            // given
            val startDate = festivalStartDate.plusDays(1)
            val endDate = festivalEndDate
            축제.changeFestivalDuration(FestivalDuration(startDate, endDate))

            // when
            val ex = shouldThrow<BadRequestException> { validator.validate(축제) }

            // then
            ex shouldHaveMessage ErrorCode.FESTIVAL_UPDATE_OUT_OF_DATE_STAGE_START_TIME.message
        }

        @Test
        fun 축제의_종료일자를_축소하면_예외가_발생한다() {
            // given
            val startDate = festivalStartDate
            val endDate = festivalEndDate.minusDays(1)
            축제.changeFestivalDuration(FestivalDuration(startDate, endDate))

            // when
            val ex = shouldThrow<BadRequestException> { validator.validate(축제) }

            // then
            ex shouldHaveMessage ErrorCode.FESTIVAL_UPDATE_OUT_OF_DATE_STAGE_START_TIME.message
        }
    }

    @Nested
    inner class 축제에_등록된_공연이_없을때 {

        @Test
        fun 축제의_일자를_확장하면_예외가_발생하지_않는다() {
            // given
            val startDate = festivalStartDate.minusDays(1)
            val endDate = festivalEndDate.plusDays(1)
            축제.changeFestivalDuration(FestivalDuration(startDate, endDate))

            // when & then
            shouldNotThrowAny { validator.validate(축제) }
        }

        @Test
        fun 축제의_시작일자를_축소하면_예외가_발생하지_않는다() {
            // given
            val startDate = festivalStartDate.plusDays(1)
            val endDate = festivalEndDate
            축제.changeFestivalDuration(FestivalDuration(startDate, endDate))

            // when & then
            shouldNotThrowAny { validator.validate(축제) }
        }

        @Test
        fun 축제의_종료일자를_축소하면_예외가_발생하지_않는다() {
            // given
            val startDate = festivalStartDate
            val endDate = festivalEndDate.minusDays(1)
            축제.changeFestivalDuration(FestivalDuration(startDate, endDate))

            // when & then
            shouldNotThrowAny { validator.validate(축제) }
        }
    }
}
