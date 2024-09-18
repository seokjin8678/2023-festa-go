package com.festago.festival.application.integration.command

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.application.command.FestivalCreateService
import com.festago.festival.dto.command.FestivalCreateCommand
import com.festago.festival.repository.FestivalQueryInfoRepository
import com.festago.festival.repository.FestivalRepository
import com.festago.school.repository.SchoolRepository
import com.festago.support.TimeInstantProvider
import com.festago.support.fixture.SchoolFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import java.time.Clock
import java.time.LocalDate

class FestivalCreateServiceIntegrationTest(
    val festivalCreateService: FestivalCreateService,
    val festivalRepository: FestivalRepository,
    val festivalQueryInfoRepository: FestivalQueryInfoRepository,
    val schoolRepository: SchoolRepository,
    val clock: Clock,
) : IntegrationDescribeSpec({

    val schoolId = schoolRepository.save(SchoolFixture.builder().build()).id
    val festivalName = "테코대학교 축제"
    val now = LocalDate.parse("2077-06-30")

    every { clock.instant() } returns TimeInstantProvider.from(now)

    describe("축제 생성") {

        context("축제의 시작일이 현재 시간보다 과거이면") {
            val command = FestivalCreateCommand(
                name = festivalName,
                startDate = now.minusDays(1),
                endDate = now.plusDays(1),
                posterImageUrl = "posterImageUrl.png",
                schoolId = schoolId
            )

            it("예외가 발생한다") {
                val ex = shouldThrow<BadRequestException> {
                    festivalCreateService.createFestival(command)
                }
                ex shouldHaveMessage ErrorCode.INVALID_FESTIVAL_START_DATE.message
            }
        }

        context("축제의 생성이 정상적으로 이뤄지면") {
            val command = FestivalCreateCommand(
                name = "테코대학교 축제",
                startDate = now.plusDays(1),
                endDate = now.plusDays(2),
                posterImageUrl = "posterImageUrl.png",
                schoolId = schoolId
            )

            val festivalId = shouldNotThrowAny { festivalCreateService.createFestival(command) }

            it("축제가 저장된다") {
                festivalRepository.findById(festivalId) shouldNotBe null
            }

            it("FestivalQueryInfo가 저장된다") {
                festivalQueryInfoRepository.findByFestivalId(festivalId) shouldNotBe null
            }
        }
    }
})