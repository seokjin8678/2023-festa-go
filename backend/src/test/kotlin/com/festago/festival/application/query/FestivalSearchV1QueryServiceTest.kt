package com.festago.festival.application.query

import com.festago.festival.dto.FestivalSearchV1Response
import com.festago.festival.infrastructure.repository.query.FestivalArtistNameSearchV1QueryDslRepository
import com.festago.festival.infrastructure.repository.query.FestivalNameSearchV1QueryDslRepository
import com.festago.support.TimeInstantProvider
import com.festago.support.spec.UnitDescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.time.Clock
import java.time.LocalDate

class FestivalSearchV1QueryServiceTest : UnitDescribeSpec({

    val now = LocalDate.parse("2077-06-30")
    val festivalArtistNameSearchV1QueryDslRepository = mockk<FestivalArtistNameSearchV1QueryDslRepository>()
    val festivalNameSearchV1QueryDslRepository = mockk<FestivalNameSearchV1QueryDslRepository>()
    val clock = spyk(Clock.systemDefaultZone())
    every { clock.instant() } returns TimeInstantProvider.from(now)

    val festivalSearchV1QueryService = FestivalSearchV1QueryService(
        festivalArtistNameSearchV1QueryDslRepository,
        festivalNameSearchV1QueryDslRepository,
        clock,
    )

    describe("아티스트 또는 축제 이름 검색 분기 조건") {

        context("키워드에 대한 아티스트가 존재하지 않으면") {

            every { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns emptyList()
            every { festivalNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns emptyList()

            festivalSearchV1QueryService.search("테코대학교")

            it("축제의 이름에 대해 검색된다") {
                verify { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) }
                verify { festivalNameSearchV1QueryDslRepository.executeSearch(any(String::class)) }
            }
        }

        context("키워드에 대한 아티스트가 존재하면") {

            every { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns listOf(
                createResponse(1L, "테코대학교 축제", now)
            )
            every { festivalNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns emptyList()

            festivalSearchV1QueryService.search("아이유")

            it("축제의 이름에 대한 검색은 호출되지 않는다") {
                verify(atLeast = 1) { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) }
                verify { festivalNameSearchV1QueryDslRepository wasNot called }
            }
        }
    }

    describe("검색 결과 정렬 조건") {

        it("결과의 첫 정렬 순서는 진행중 - 예정 - 종료된 축제로 정렬된다") {
            val expect = listOf(
                createResponse(3L, "종료 축제", now.minusDays(1)),
                createResponse(1L, "진행중 축제", now),
                createResponse(2L, "예정 축제", now.plusDays(1)),
            )
            every { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns expect

            val actual = festivalSearchV1QueryService.search("아이유")

            actual.map { it.name } shouldBe listOf("진행중 축제", "예정 축제", "종료 축제")
        }

        context("진행중 축제가 여러개 일 때") {
            val expect = listOf(
                createResponse(3L, "진행중 축제 3", now),
                createResponse(2L, "진행중 축제 2", now),
                createResponse(1L, "진행중 축제 1", now),
            )
            every { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns expect

            val actual = festivalSearchV1QueryService.search("아이유")

            it("시작 시간 오름차순으로 정렬되고 시작 시간이 같으면 식별자 오름차순으로 정렬된다") {
                actual.map { it.name } shouldBe listOf(
                    "진행중 축제 1",
                    "진행중 축제 2",
                    "진행중 축제 3",
                )
            }
        }

        context("종료 축제가 여러개 일 때") {
            val expect = listOf(
                createResponse(1L, "종료 축제 1", now.minusDays(2)),
                createResponse(2L, "종료 축제 2", now.minusDays(1)),
                createResponse(3L, "종료 축제 3", now.minusDays(1)),
            )
            every { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns expect

            val actual = festivalSearchV1QueryService.search("아이유")

            it("종료 시간 내림차순으로 정렬되고 종료 시간이 같으면 식별자 오름차순으로 정렬된다") {
                actual.map { it.name } shouldBe listOf(
                    "종료 축제 2",
                    "종료 축제 3",
                    "종료 축제 1",
                )
            }
        }

        context("예정 축제가 여러개 일 때") {
            val expect = listOf(
                createResponse(1L, "예정 축제 1", now.plusDays(2)),
                createResponse(2L, "예정 축제 2", now.plusDays(2)),
                createResponse(3L, "예정 축제 3", now.plusDays(1)),
            )
            every { festivalArtistNameSearchV1QueryDslRepository.executeSearch(any(String::class)) } returns expect

            val actual = festivalSearchV1QueryService.search("아이유")

            it("시작 시간 오름차순으로 정렬되고 시작 시간이 같으면 식별자 오름차순으로 정렬된다") {
                actual.map { it.name } shouldBe listOf(
                    "예정 축제 3",
                    "예정 축제 1",
                    "예정 축제 2",
                )
            }
        }
    }
})

private fun createResponse(id: Long, name: String, startDate: LocalDate): FestivalSearchV1Response {
    return FestivalSearchV1Response(id, name, startDate, startDate, "", "[]")
}
