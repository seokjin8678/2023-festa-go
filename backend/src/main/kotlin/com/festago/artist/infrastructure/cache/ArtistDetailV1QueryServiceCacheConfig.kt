package com.festago.artist.infrastructure.cache

import com.festago.artist.application.ArtistDetailV1QueryService
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import com.festago.common.cache.CacheEvictCommandEvent
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.config.CaffeineCacheBuilder
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
private class ArtistDetailV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun artistDetailV1QueryServiceArtistDetailCache(): Cache {
        return CaffeineCacheBuilder.build(ArtistDetailV1QueryService.ARTIST_DETAIL_CACHE_NAME)
    }

    @Bean
    fun artistDetailV1QueryServiceArtistDetailFestivalsCache(): Cache {
        return CaffeineCacheBuilder.build(ArtistDetailV1QueryService.ARTIST_DETAIL_CACHE_NAME)
    }

    @EventListener
    fun evictWhenArtistUpdated(event: ArtistUpdatedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                ArtistDetailV1QueryService.ARTIST_DETAIL_CACHE_NAME,
                event.artist.identifier.toString()
            )
        )
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME,
                event.artist.identifier.toString()
            )
        )
    }

    @EventListener
    fun evictWhenArtistDeleted(event: ArtistDeletedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                ArtistDetailV1QueryService.ARTIST_DETAIL_CACHE_NAME,
                event.artistId.toString()
            )
        )
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME,
                event.artistId.toString()
            )
        )
    }

    @EventListener(value = [FestivalUpdatedEvent::class, FestivalDeletedEvent::class])
    fun invalidateCacheWhenFestivalModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME))
    }

    @EventListener(value = [StageCreatedEvent::class, StageUpdatedEvent::class, StageDeletedEvent::class])
    fun invalidateCacheWhenStageModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME))
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun invalidateCacheAtMidnight() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME))
    }
}
