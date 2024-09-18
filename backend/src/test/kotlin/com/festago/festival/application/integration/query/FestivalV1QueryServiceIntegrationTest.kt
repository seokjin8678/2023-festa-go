package com.festago.festival.application.integration.query

import com.festago.festival.application.query.FestivalV1QueryService
import com.festago.festival.domain.Festival
import com.festago.festival.dto.FestivalV1QueryRequest
import com.festago.festival.repository.FestivalFilter
import com.festago.festival.repository.FestivalQueryInfoRepository
import com.festago.festival.repository.FestivalRepository
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRegion
import com.festago.school.repository.SchoolRepository
import com.festago.support.TimeInstantProvider
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.FestivalQueryInfoFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import java.time.Clock
import java.time.LocalDate
import org.springframework.data.domain.Pageable

/**
 * 현재 시간
 * - 2077년 7월 10일
 *
 * 식별자는 순서대로 오름차순
 *
 * 진행 중 축제 5개
 * - 서울대학교 8~12일
 * - 서울대학교 6~12일
 * - 대구대학교 9~12일
 * - 부산대학교 6~13일
 * - 부산대학교 6~12일
 *
 * 진행 예정 축제 3개
 * - 대구대학교 13~14일
 * - 대구대학교 12~14일
 * - 부산대학교 12~14일
 *
 * 진행 종료 축제 3개
 * - 서울대학교 8~9일
 * - 부산대학교 7~8일
 */
class FestivalV1QueryServiceIntegrationTest(
    val festivalV1QueryService: FestivalV1QueryService,
    val festivalRepository: FestivalRepository,
    val schoolRepository: SchoolRepository,
    val festivalQueryInfoRepository: FestivalQueryInfoRepository,
    val clock: Clock,
) : IntegrationDescribeSpec({

    val now: LocalDate = LocalDate.parse("2077-07-10")

    every { clock.instant() } returns TimeInstantProvider.from(now)

    fun createSchool(name: String, region: SchoolRegion): School {
        return schoolRepository.save(SchoolFixture.builder().name(name).region(region).build())
    }

    fun createFestival(name: String, startDate: LocalDate, endDate: LocalDate, school: School): Festival {
        val festival = festivalRepository.save(
            FestivalFixture.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .school(school)
                .build()
        )
        festivalQueryInfoRepository.save(FestivalQueryInfoFixture.builder().festivalId(festival.id).build())
        return festival
    }

    val 서울대학교 = createSchool("서울대학교", SchoolRegion.서울)
    val 부산대학교 = createSchool("부산대학교", SchoolRegion.부산)
    val 대구대학교 = createSchool("대구대학교", SchoolRegion.대구)

    val 서울대학교_8일_12일_축제 = createFestival("서울대학교_8일_12일_축제", now.minusDays(2), now.plusDays(2), 서울대학교)
    val 서울대학교_6일_12일_축제 = createFestival("서울대학교_6일_12일_축제", now.minusDays(4), now.plusDays(2), 서울대학교)
    val 대구대학교_9일_12일_축제 = createFestival("대구대학교_9일_12일_축제", now.minusDays(1), now.plusDays(2), 대구대학교)
    val 부산대학교_6일_13일_축제 = createFestival("부산대학교_6일_13일_축제", now.minusDays(4), now.plusDays(3), 부산대학교)
    val 부산대학교_6일_12일_축제 = createFestival("부산대학교_6일_12일_축제", now.minusDays(4), now.plusDays(2), 부산대학교)

    val 대구대학교_13일_14일_축제 = createFestival("대구대학교_13일_14일_축제", now.plusDays(3), now.plusDays(4), 대구대학교)
    val 대구대학교_12일_14일_축제 = createFestival("대구대학교_12일_14일_축제", now.plusDays(2), now.plusDays(4), 대구대학교)
    val 부산대학교_12일_14일_축제 = createFestival("부산대학교_12일_14일_축제", now.plusDays(2), now.plusDays(4), 부산대학교)

    val 서울대학교_8일_9일_축제 = createFestival("서울대학교_8일_9일_축제", now.minusDays(2), now.minusDays(1), 서울대학교)
    val 부산대학교_7일_8일_축제 = createFestival("부산대학교_7일_9일_축제", now.minusDays(3), now.minusDays(2), 부산대학교)

    describe("지역 필터링 조건 검사") {
        context("지역 필터링 없이 축제를 조회하면") {
            val region = SchoolRegion.ANY

            it("진행 중인 축제는 5개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PROGRESS)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 5
            }

            it("진행 예정인 축제는 3개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PLANNED)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)
                expect.content.size shouldBe 3
            }

            it("진행 종료인 축제는 2개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.END)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 2
            }
        }

        context("대구 지역으로 축제를 조회하면") {
            val region = SchoolRegion.대구

            it("진행 중인 축제는 1개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PROGRESS)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 1
            }

            it("진행 예정인 축제는 2개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PLANNED)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 2
            }

            it("진행 종료인 축제는 0개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.END)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 0
            }
        }

        context("서울 지역으로 축제를 조회하면") {
            val region = SchoolRegion.서울

            it("진행 중인 축제는 2개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PROGRESS)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 2
            }

            it("진행 예정인 축제는 0개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PLANNED)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 0
            }

            it("진행 종료인 축제는 1개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.END)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 1
            }
        }

        context("부산 지역으로 축제를 조회하면") {
            val region = SchoolRegion.부산

            it("진행 중인 축제는 2개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PROGRESS)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 2
            }

            it("진행 예정인 축제는 1개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.PLANNED)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 1
            }

            it("진행 종료인 축제는 1개이다") {
                val request = FestivalV1QueryRequest(region, FestivalFilter.END)
                val expect = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

                expect.content.size shouldBe 1
            }
        }
    }

    describe("정렬 조건 검사") {
        context("진행 예정 축제를 조회하면") {
            val request = FestivalV1QueryRequest(SchoolRegion.ANY, FestivalFilter.PLANNED)
            val response = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

            it("시작일 오름차순, 식별자 오름차순 정렬된다") {
                response.content.map { it.id } shouldContainExactly listOf(
                    대구대학교_12일_14일_축제.id,
                    부산대학교_12일_14일_축제.id,
                    대구대학교_13일_14일_축제.id,
                )
            }
        }

        context("진행 중 축제를 조회하면") {
            val request = FestivalV1QueryRequest(SchoolRegion.ANY, FestivalFilter.PROGRESS)
            val response = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

            it("시작일 내림차순, 식별자 오름차순 정렬된다") {
                response.content.map { it.id } shouldContainExactly listOf(
                    대구대학교_9일_12일_축제.id,
                    서울대학교_8일_12일_축제.id,
                    서울대학교_6일_12일_축제.id,
                    부산대학교_6일_13일_축제.id,
                    부산대학교_6일_12일_축제.id,
                )
            }
        }

        context("진행 종료 축제를 조회하면") {
            val request = FestivalV1QueryRequest(SchoolRegion.ANY, FestivalFilter.END)
            val response = festivalV1QueryService.findFestivals(Pageable.ofSize(10), request)

            it("종료일 내림차순 정렬된다") {
                response.content.map { it.id } shouldContainExactly listOf(
                    서울대학교_8일_9일_축제.id,
                    부산대학교_7일_8일_축제.id,
                )
            }
        }
    }

    describe("커서 기반 페이징 검사") {
        context("커서 기반 페이징으로 조회하면") {
            val firstRequest = FestivalV1QueryRequest(SchoolRegion.ANY, FestivalFilter.PROGRESS)
            val firstResponse = festivalV1QueryService.findFestivals(Pageable.ofSize(2), firstRequest)
            val lastElement = firstResponse.content.last()
            val secondRequest =
                firstRequest.copy(lastFestivalId = lastElement.id, lastStartDate = lastElement.startDate)
            val secondResponse = festivalV1QueryService.findFestivals(Pageable.ofSize(5), secondRequest)

            it("커서 기반 페이징의 결과가 올바르게 반환되어야 한다") {
                firstResponse.hasNext() shouldBe true
                firstResponse.content.size shouldBe 2
                secondResponse.hasNext() shouldBe false
                secondResponse.content.size shouldBe 3
            }
        }
    }
})