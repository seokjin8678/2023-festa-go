package com.festago.festival.domain

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.support.spec.UnitDescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDate

class FestivalDurationTest : UnitDescribeSpec({

    val _6월_15일 = LocalDate.parse("2077-06-15")
    val _6월_16일 = LocalDate.parse("2077-06-16")
    val _6월_17일 = LocalDate.parse("2077-06-17")

    describe("생성자 검증") {

        context("시작일이 종료일 이전이면") {

            it("예외가 발생한다") {
                val ex = shouldThrow<BadRequestException> { FestivalDuration(_6월_17일, _6월_16일) }
                ex shouldHaveMessage ErrorCode.INVALID_FESTIVAL_DURATION.message
            }
        }
    }

    describe("isStartDateBeforeTo") {

        context("시작일이 주어진 날짜보다 이후이면") {
            val _6월_16일_6월_16일 = FestivalDuration(_6월_16일, _6월_16일)
            val actual = _6월_16일_6월_16일.isStartDateBeforeTo(_6월_15일)

            it("거짓이 반환된다") {
                actual shouldBe false
            }
        }

        context("시작일이 주어진 날짜와 같으면") {
            val _6월_16일_6월_16일 = FestivalDuration(_6월_16일, _6월_16일)
            val actual = _6월_16일_6월_16일.isStartDateBeforeTo(_6월_16일)

            it("거짓이 반환된다") {
                actual shouldBe false
            }
        }

        context("시작일이 주어진 날짜 이전이면") {
            val _6월_16일_6월_16일 = FestivalDuration(_6월_16일, _6월_16일)
            val actual = _6월_16일_6월_16일.isStartDateBeforeTo(_6월_17일)

            it("참이 반환된다") {
                actual shouldBe true
            }
        }
    }

    describe("isNotInDuration") {
        context("기간에 포함되면") {
            val _6월_15일_6월_17일 = FestivalDuration(_6월_15일, _6월_17일)
            val actual = _6월_15일_6월_17일.isNotInDuration(_6월_16일)

            it("거짓이 반환된다") {
                actual shouldBe false
            }
        }

        context("기간에 포함되지 않으면") {
            val _6월_16일_6월_17일 = FestivalDuration(_6월_16일, _6월_17일)
            val actual = _6월_16일_6월_17일.isNotInDuration(_6월_15일)

            it("참이 반환된다") {
                actual shouldBe true
            }
        }
    }
})