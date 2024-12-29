package com.festago.artist.application

import com.festago.artist.dto.ArtistSearchStageCountV1Response
import com.festago.artist.dto.ArtistTotalSearchV1Response
import com.festago.artist.dto.event.ArtistCreatedEvent
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import com.festago.common.cache.CacheInvalidateCommandEvent
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
class ArtistTotalSearchV1Service(
    private val artistSearchV1QueryService: ArtistSearchV1QueryService,
    private val artistSearchStageCountV1QueryService: ArtistSearchStageCountV1QueryService,
    private val clock: Clock,
) {

    @Cacheable(cacheNames = [CACHE_NAME], key = "#keyword")
    fun findAllByKeyword(keyword: String): List<ArtistTotalSearchV1Response> {
        val artists = artistSearchV1QueryService.findAllByKeyword(keyword)
        val artistIdToStageCount = artistSearchStageCountV1QueryService.findArtistsStageCountAfterDateTime(
            artistIds = artists.map { it.id },
            now = LocalDate.now(clock).atStartOfDay()
        )
        return artists.map {
            ArtistTotalSearchV1Response.of(
                artistResponse = it,
                stageCount = artistIdToStageCount[it.id] ?: ArtistSearchStageCountV1Response.EMPTY
            )
        }
    }

    companion object {
        const val CACHE_NAME = "ARTIST_TOTAL_SEARCH_V1"
    }
}

@Configuration
class ArtistTotalSearchV1ServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun artistTotalSearchV1ServiceCache(): Cache {
        return CaffeineCache(
            ArtistTotalSearchV1Service.CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10)
                .build()
        )
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
