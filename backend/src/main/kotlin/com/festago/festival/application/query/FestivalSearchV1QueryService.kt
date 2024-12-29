package com.festago.festival.application.query

import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.festival.dto.FestivalSearchV1Response
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.festival.infrastructure.repository.query.FestivalArtistNameSearchV1QueryDslRepository
import com.festago.festival.infrastructure.repository.query.FestivalFilter
import com.festago.festival.infrastructure.repository.query.FestivalNameSearchV1QueryDslRepository
import com.festago.stage.dto.event.StageCreatedEvent
import com.festago.stage.dto.event.StageDeletedEvent
import com.festago.stage.dto.event.StageUpdatedEvent
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import org.springframework.cache.Cache
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FestivalSearchV1QueryService(
    private val festivalArtistNameSearchV1QueryDslRepository: FestivalArtistNameSearchV1QueryDslRepository,
    private val festivalNameSearchV1QueryDslRepository: FestivalNameSearchV1QueryDslRepository,
    private val clock: Clock,
) {

    @Cacheable(cacheNames = [CACHE_NAME], key = "#keyword")
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
        const val CACHE_NAME = "FESTIVAL_SEARCH_V1"
    }
}

@Configuration
class FestivalSearchV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun festivalSearchV1QueryServiceCache(): Cache {
        return CaffeineCache(
            FestivalSearchV1QueryService.CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10)
                .build()
        )
    }

    @EventListener(value = [FestivalCreatedEvent::class, FestivalUpdatedEvent::class, FestivalDeletedEvent::class])
    fun invalidateCacheWhenFestivalModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalSearchV1QueryService.CACHE_NAME))
    }

    @EventListener(value = [StageCreatedEvent::class, StageUpdatedEvent::class, StageDeletedEvent::class])
    fun invalidateCacheWhenStageModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalSearchV1QueryService.CACHE_NAME))
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun invalidateCacheAtMidnight() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalSearchV1QueryService.CACHE_NAME))
    }
}
