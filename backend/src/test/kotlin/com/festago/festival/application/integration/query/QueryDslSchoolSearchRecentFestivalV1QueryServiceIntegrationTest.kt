package com.festago.festival.application.integration.query

import com.festago.festival.application.query.QueryDslSchoolUpcomingFestivalStartDateV1QueryService
import com.festago.festival.domain.FestivalRepository
import com.festago.school.domain.SchoolRepository
import com.festago.support.TimeInstantProvider
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import java.time.Clock
import java.time.LocalDate

/**
 * 테코대학교에 6월 15일 ~ 6월 15일 축제, 6월 16일 ~ 6월 16일 축제
 *
 * 우테대학교에 6월 16일 ~ 6월 17일 축제
 */
class QueryDslSchoolSearchRecentFestivalV1QueryServiceIntegrationTest(
    schoolUpcomingFestivalStartDateV1QueryService: QueryDslSchoolUpcomingFestivalStartDateV1QueryService,
    schoolRepository: SchoolRepository,
    festivalRepository: FestivalRepository,
    clock: Clock,
) : IntegrationDescribeSpec({

    val _6월_14일 = LocalDate.parse("2077-06-14")
    val _6월_15일 = LocalDate.parse("2077-06-15")
    val _6월_16일 = LocalDate.parse("2077-06-16")
    val _6월_17일 = LocalDate.parse("2077-06-17")
    val _6월_18일 = LocalDate.parse("2077-06-18")

    val 테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())
    val 우테대학교 = schoolRepository.save(SchoolFixture.builder().name("우테대학교").build())

    festivalRepository.save(
        FestivalFixture.builder()
            .name("테코대학교 6월 15일 당일 축제")
            .startDate(_6월_15일)
            .endDate(_6월_15일)
            .school(테코대학교)
            .build()
    )
    festivalRepository.save(
        FestivalFixture.builder()
            .name("테코대학교 6월 16일 당일 축제")
            .startDate(_6월_16일)
            .endDate(_6월_16일)
            .school(테코대학교)
            .build()
    )
    festivalRepository.save(
        FestivalFixture.builder()
            .name("우테대학교 6월 16~17일 축제")
            .startDate(_6월_16일)
            .endDate(_6월_17일)
            .school(우테대학교)
            .build()
    )

    describe("학교의 식별자로 해당 학교의 축제 중 종료되지 않고 곧 시작할 축제 시작일 조회") {
        context("오늘이 6월 14일이면") {
            every { clock.instant() } returns TimeInstantProvider.from(_6월_14일)

            val actual = schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(
                listOf(테코대학교.identifier, 우테대학교.identifier)
            )

            it("테코대학교는 6월 15일이 조회된다") {
                actual[테코대학교.id] shouldBe _6월_15일
            }

            it("우테대학교는 6월 16일이 조회된다") {
                actual[우테대학교.id] shouldBe _6월_16일
            }
        }

        context("오늘이 6월 15일이면") {
            every { clock.instant() } returns TimeInstantProvider.from(_6월_15일)

            val actual = schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(
                listOf(테코대학교.identifier, 우테대학교.identifier)
            )

            it("테코대학교는 6월 15일이 조회된다") {
                actual[테코대학교.id] shouldBe _6월_15일
            }

            it("우테대학교는 6월 16일이 조회된다") {
                actual[우테대학교.id] shouldBe _6월_16일
            }
        }

        context("오늘이 6월 16일이면") {
            every { clock.instant() } returns TimeInstantProvider.from(_6월_16일)

            val actual = schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(
                listOf(테코대학교.identifier, 우테대학교.identifier)
            )

            it("테코대학교는 6월 16일이 조회된다") {
                actual[테코대학교.id] shouldBe _6월_16일
            }

            it("우테대학교는 6월 16일이 조회된다") {
                actual[우테대학교.id] shouldBe _6월_16일
            }
        }

        context("오늘이 6월 17일이면") {
            every { clock.instant() } returns TimeInstantProvider.from(_6월_17일)

            val actual = schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(
                listOf(테코대학교.identifier, 우테대학교.identifier)
            )

            it("테코대학교는 null이 조회된다") {
                actual[테코대학교.id] shouldBe null
            }

            it("우테대학교는 6월 16일이 조회된다") {
                actual[우테대학교.id] shouldBe _6월_16일
            }
        }

        context("오늘이 6월 18일이면") {
            every { clock.instant() } returns TimeInstantProvider.from(_6월_18일)

            val actual = schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(
                listOf(테코대학교.identifier, 우테대학교.identifier)
            )

            it("테코대학교는 null이 조회된다") {
                actual[테코대학교.id] shouldBe null
            }

            it("우테대학교는 null이 조회된다") {
                actual[우테대학교.id] shouldBe null
            }
        }
    }
})
