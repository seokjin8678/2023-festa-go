package com.festago.festival.infrastructure.cache

import com.festago.common.cache.CacheFactory
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.festival.application.query.FestivalV1QueryService
import com.festago.festival.dto.event.FestivalCreatedEvent
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
private class FestivalV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
    private val cacheFactory: CacheFactory,
) {

    @Bean
    fun festivalV1QueryServiceCache(): Cache {
        return cacheFactory.create(FestivalV1QueryService.CACHE_NAME) {
            recordStats = false
        }
    }

    @EventListener(value = [FestivalCreatedEvent::class, FestivalUpdatedEvent::class, FestivalDeletedEvent::class])
    fun invalidateCacheWhenFestivalModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalV1QueryService.CACHE_NAME))
    }

    @EventListener(value = [StageCreatedEvent::class, StageUpdatedEvent::class, StageDeletedEvent::class])
    fun invalidateCacheWhenStageModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalV1QueryService.CACHE_NAME))
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun invalidateCacheAtMidnight() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(FestivalV1QueryService.CACHE_NAME))
    }
}
