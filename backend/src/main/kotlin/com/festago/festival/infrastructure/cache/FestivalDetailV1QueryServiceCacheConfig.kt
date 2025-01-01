package com.festago.festival.infrastructure.cache

import com.festago.common.cache.CacheEvictCommandEvent
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.common.cache.caffeineCache
import com.festago.festival.application.query.FestivalDetailV1QueryService
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.stage.dto.event.StageCreatedEvent
import com.festago.stage.dto.event.StageDeletedEvent
import com.festago.stage.dto.event.StageUpdatedEvent
import org.springframework.cache.Cache
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled

@Configuration
private class FestivalDetailV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun festivalDetailV1QueryServiceCache(): Cache {
        return caffeineCache(FestivalDetailV1QueryService.CACHE_NAME) {
            maximumSize = 20
        }
    }

    @EventListener
    fun evictWhenFestivalUpdated(event: FestivalUpdatedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                FestivalDetailV1QueryService.CACHE_NAME,
                event.festival.identifier.toString()
            )
        )
    }

    @EventListener
    fun evictWhenFestivalDeleted(event: FestivalDeletedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                FestivalDetailV1QueryService.CACHE_NAME,
                event.festivalId.toString()
            )
        )
    }

    @EventListener
    fun evictWhenStageCreated(event: StageCreatedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                FestivalDetailV1QueryService.CACHE_NAME,
                event.stage.festival.identifier.toString()
            )
        )
    }

    @EventListener
    fun evictWhenStageUpdated(event: StageUpdatedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                FestivalDetailV1QueryService.CACHE_NAME,
                event.stage.festival.identifier.toString()
            )
        )
    }

    @EventListener
    fun evictWhenStageDeleted(event: StageDeletedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                FestivalDetailV1QueryService.CACHE_NAME,
                event.stage.festival.identifier.toString()
            )
        )
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun invalidateCacheAtMidnight() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalDetailV1QueryService.CACHE_NAME))
    }
}
