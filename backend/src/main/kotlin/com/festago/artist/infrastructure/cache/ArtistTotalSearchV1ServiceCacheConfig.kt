package com.festago.artist.infrastructure.cache

import com.festago.artist.application.ArtistTotalSearchV1Service
import com.festago.artist.dto.event.ArtistCreatedEvent
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.common.cache.caffeineCache
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
private class ArtistTotalSearchV1ServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun artistTotalSearchV1ServiceCache(): Cache {
        return caffeineCache(ArtistTotalSearchV1Service.CACHE_NAME)
    }

    @EventListener(value = [ArtistCreatedEvent::class, ArtistUpdatedEvent::class, ArtistDeletedEvent::class])
    fun invalidateCacheWhenArtistModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(ArtistTotalSearchV1Service.CACHE_NAME))
    }

    @EventListener(value = [StageCreatedEvent::class, StageUpdatedEvent::class, StageDeletedEvent::class])
    fun invalidateCacheWhenStageModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(ArtistTotalSearchV1Service.CACHE_NAME))
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun invalidateCacheAtMidnight() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(ArtistTotalSearchV1Service.CACHE_NAME))
    }
}
