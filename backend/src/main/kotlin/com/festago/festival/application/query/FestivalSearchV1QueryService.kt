package com.festago.festival.application.query

import com.festago.festival.dto.FestivalSearchV1Response
import com.festago.festival.repository.FestivalArtistNameSearchV1QueryDslRepository
import com.festago.festival.repository.FestivalFilter
import com.festago.festival.repository.FestivalNameSearchV1QueryDslRepository
import java.time.Clock
import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FestivalSearchV1QueryService(
    private val festivalArtistNameSearchV1QueryDslRepository: FestivalArtistNameSearchV1QueryDslRepository,
    private val festivalNameSearchV1QueryDslRepository: FestivalNameSearchV1QueryDslRepository,
    private val clock: Clock,
) {

    fun search(keyword: String): List<FestivalSearchV1Response> {
        val festivals = findFestivals(keyword)
        return festivals.getSortedResult()
    }

    private fun findFestivals(keyword: String): List<FestivalSearchV1Response> {
        val result = festivalArtistNameSearchV1QueryDslRepository.executeSearch(keyword)
        return result.ifEmpty { festivalNameSearchV1QueryDslRepository.executeSearch(keyword) }
    }

    private fun List<FestivalSearchV1Response>.getSortedResult(): List<FestivalSearchV1Response> {
        val filterToFestivals = this.groupBy { mapToFilterByTime(it) }
        val result = mutableListOf<FestivalSearchV1Response>()
        for (filter in FILTERS) {
            val sortedFestivals = filterToFestivals.getOrDefault(filter, emptyList())
                .sortedWith(getComparatorByFilter(filter))
            result.addAll(sortedFestivals)
        }
        return result
    }

    private fun mapToFilterByTime(festival: FestivalSearchV1Response): FestivalFilter {
        val now = LocalDate.now(clock)
        return when {
            now > festival.endDate -> FestivalFilter.END
            now < festival.startDate -> FestivalFilter.PLANNED
            else -> FestivalFilter.PROGRESS
        }
    }

    private fun getComparatorByFilter(status: FestivalFilter): Comparator<FestivalSearchV1Response> {
        return when (status) {
            FestivalFilter.END -> Comparator.comparing(FestivalSearchV1Response::endDate).reversed()
            FestivalFilter.PROGRESS, FestivalFilter.PLANNED -> Comparator.comparing(FestivalSearchV1Response::startDate)
        }.thenComparing(Comparator.comparing(FestivalSearchV1Response::id))
    }

    companion object {
        // 해당 리스트의 순서로 정렬 순서가 결정되므로 수정 금지
        private val FILTERS = listOf(FestivalFilter.PROGRESS, FestivalFilter.PLANNED, FestivalFilter.END)
    }
}
