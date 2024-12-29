package com.festago.festival.application.query

import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.festival.dto.PopularFestivalsV1Response
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.festival.infrastructure.repository.query.PopularFestivalV1QueryDslRepository
import com.festago.stage.dto.event.StageCreatedEvent
import com.festago.stage.dto.event.StageDeletedEvent
import com.festago.stage.dto.event.StageUpdatedEvent
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import org.springframework.cache.Cache
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PopularFestivalV1QueryService(
    private val popularFestivalRepository: PopularFestivalV1QueryDslRepository,
) {

    @Cacheable(cacheNames = [CACHE_NAME])
    fun findPopularFestivals(): PopularFestivalsV1Response {
        val popularFestivals = popularFestivalRepository.findPopularFestivals()
        return PopularFestivalsV1Response("요즘 뜨는 축제", popularFestivals)
    }

    companion object {
        const val CACHE_NAME = "POPULAR_FESTIVALS_V1"
    }
}

@Configuration
class PopularFestivalV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun popularFestivalV1QueryServiceCache(): Cache {
        return CaffeineCache(
            PopularFestivalV1QueryService.CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(1)
                .build()
        )
    }

    @EventListener(value = [FestivalCreatedEvent::class, FestivalUpdatedEvent::class, FestivalDeletedEvent::class])
    fun invalidateCacheWhenFestivalModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(PopularFestivalV1QueryService.CACHE_NAME))
    }

    @EventListener(value = [StageCreatedEvent::class, StageUpdatedEvent::class, StageDeletedEvent::class])
    fun invalidateCacheWhenStageModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(PopularFestivalV1QueryService.CACHE_NAME))
    }
}
