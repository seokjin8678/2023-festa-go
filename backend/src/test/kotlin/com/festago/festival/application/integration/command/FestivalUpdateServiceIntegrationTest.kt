package com.festago.festival.application.integration.command

import com.festago.festival.application.command.FestivalUpdateService
import com.festago.festival.dto.command.FestivalUpdateCommand
import com.festago.festival.repository.FestivalRepository
import com.festago.school.repository.SchoolRepository
import com.festago.support.TimeInstantProvider
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import java.time.Clock
import java.time.LocalDate
import org.junit.jupiter.api.assertDoesNotThrow

class FestivalUpdateServiceIntegrationTest(
    val festivalUpdateService: FestivalUpdateService,
    val schoolRepository: SchoolRepository,
    val festivalRepository: FestivalRepository,
    val clock: Clock,
) : IntegrationDescribeSpec({

    val school = schoolRepository.save(SchoolFixture.builder().build())
    val festival = festivalRepository.save(FestivalFixture.builder().school(school).build())
    val festivalId = festival.id!!
    val now = LocalDate.parse("2023-01-31")

    every { clock.instant() } returns TimeInstantProvider.from(now)

    describe("축제 수정") {

        context("시작일이 현재 시간보다 과거여도") {
            val command = FestivalUpdateCommand(
                name = "변경된 축제",
                startDate = now.minusDays(1),
                endDate = now.plusDays(1),
                posterImageUrl = "https://image.com/new-image.png"
            )

            it("예외가 발생하지 않고 수정된다") {
                assertDoesNotThrow { festivalUpdateService.updateFestival(festivalId, command) }
                festivalRepository.findById(festivalId)!!.name shouldBe command.name
            }
        }

        context("축제를 정상적으로 수정하면") {
            val command = FestivalUpdateCommand(
                name = "변경된 축제",
                startDate = now.plusDays(1),
                endDate = now.plusDays(2),
                posterImageUrl = "https://image.com/new-image.png"
            )

            festivalUpdateService.updateFestival(festivalId, command)

            it("저장된 축제의 데이터가 수정된다") {
                festivalRepository.findById(festivalId)!!.name shouldBe command.name
            }
        }
    }
})