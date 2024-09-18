package com.festago.festival.application.integration.command

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.application.command.FestivalDeleteService
import com.festago.festival.repository.FestivalQueryInfoRepository
import com.festago.festival.repository.FestivalRepository
import com.festago.school.repository.SchoolRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.StageFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class FestivalDeleteServiceIntegrationTest(
    val festivalDeleteService: FestivalDeleteService,
    val festivalQueryInfoRepository: FestivalQueryInfoRepository,
    val festivalRepository: FestivalRepository,
    val schoolRepository: SchoolRepository,
    val stageRepository: StageRepository,
) : IntegrationDescribeSpec({

    val school = schoolRepository.save(SchoolFixture.builder().build())
    val festival = festivalRepository.save(FestivalFixture.builder().school(school).build())
    val festivalId = festival.id!!

    describe("축제 삭제") {

        context("공연이 등록된 축제를 삭제하면") {

            stageRepository.save(StageFixture.builder().festival(festival).build())

            it("예외가 발생한다") {
                val ex = shouldThrow<BadRequestException> {
                    festivalDeleteService.deleteFestival(festivalId)
                }
                ex shouldHaveMessage ErrorCode.FESTIVAL_DELETE_CONSTRAINT_EXISTS_STAGE.message
            }
        }

        context("축제가 정상적으로 삭제되면") {

            festivalDeleteService.deleteFestival(festivalId)

            it("저장된 축제가 삭제된다") {
                festivalRepository.findById(festivalId) shouldBe null
            }

            it("FestivalQueryInfo도 삭제된다") {
                festivalQueryInfoRepository.findByFestivalId(festivalId) shouldBe null
            }
        }
    }
})
