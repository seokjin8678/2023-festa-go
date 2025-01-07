package com.festago.festival.infrastructure.cache

import com.festago.common.cache.CacheFactory
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.festival.application.query.PopularFestivalV1QueryService
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

@Configuration
private class PopularFestivalV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
    private val cacheFactory: CacheFactory,
) {

    @Bean
    fun popularFestivalV1QueryServiceCache(): Cache {
        return cacheFactory.create(PopularFestivalV1QueryService.CACHE_NAME) {
            maximumSize = 1
            recordStats = false
        }
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
