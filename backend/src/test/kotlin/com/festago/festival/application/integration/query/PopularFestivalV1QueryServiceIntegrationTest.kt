package com.festago.festival.application.integration.query

import com.festago.festival.application.query.PopularFestivalV1QueryService
import com.festago.festival.domain.Festival
import com.festago.festival.repository.FestivalQueryInfoRepository
import com.festago.festival.repository.FestivalRepository
import com.festago.school.repository.SchoolRepository
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.FestivalQueryInfoFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import java.time.LocalDate

class PopularFestivalV1QueryServiceIntegrationTest(
    val popularFestivalV1QueryService: PopularFestivalV1QueryService,
    val schoolRepository: SchoolRepository,
    val festivalRepository: FestivalRepository,
    val festivalQueryInfoRepository: FestivalQueryInfoRepository,
) : IntegrationDescribeSpec({

    val 대학교 = schoolRepository.save(SchoolFixture.builder().build())
    val startDate = LocalDate.parse("2077-06-30")

    fun createFestival(startDate: LocalDate): Festival {
        val festival = festivalRepository.save(
            FestivalFixture.builder()
                .school(대학교)
                .startDate(startDate)
                .endDate(startDate)
                .build()
        )
        festivalQueryInfoRepository.save(
            FestivalQueryInfoFixture.builder()
                .festivalId(festival.id)
                .artistInfo("""[{"foo":"bar"}]""")
                .build()
        )
        return festival
    }

    fun createEmptyStagesFestival(startDate: LocalDate): Festival {
        val festival = festivalRepository.save(
            FestivalFixture.builder()
                .school(대학교)
                .startDate(startDate)
                .endDate(startDate)
                .build()
        )
        festivalQueryInfoRepository.save(
            FestivalQueryInfoFixture.builder()
                .festivalId(festival.id)
                .build()
        )
        return festival
    }

    val 첫번째로_저장된_축제 = createFestival(startDate)
    val 두번째로_저장된_축제 = createFestival(startDate)
    val 세번째로_저장된_축제 = createFestival(startDate)
    val 네번째로_저장된_축제 = createFestival(startDate)
    val 다섯번째로_저장된_축제 = createFestival(startDate)
    val 여섯번째로_저장된_축제 = createFestival(startDate)
    val 일곱번째로_저장된_축제 = createFestival(startDate)
    val 여덟번째로_저장된_축제 = createFestival(startDate)
    val 아홉번째로_저장된_공연없는_축제 = createEmptyStagesFestival(startDate)
    val 열번째로_저장된_공연없는_축제 = createEmptyStagesFestival(startDate)
    val 열한번쨰로_저장된_기간이_지난_축제 = createFestival(startDate.minusDays(10L))

    describe("인기 축제 조회") {

        val expect = popularFestivalV1QueryService.findPopularFestivals()

        it("공연이 등록된 축제 중 7개까지 조회되고 식별자의 내림차순으로 정렬된다") {
            expect.content.map { it.id } shouldContainExactly listOf(
                열한번쨰로_저장된_기간이_지난_축제.id,
                여덟번째로_저장된_축제.id,
                일곱번째로_저장된_축제.id,
                여섯번째로_저장된_축제.id,
                다섯번째로_저장된_축제.id,
                네번째로_저장된_축제.id,
                세번째로_저장된_축제.id
            )
        }
    }
})
