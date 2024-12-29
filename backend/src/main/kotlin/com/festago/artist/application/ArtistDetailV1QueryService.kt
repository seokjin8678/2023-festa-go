package com.festago.artist.application

import com.festago.artist.dto.ArtistDetailV1Response
import com.festago.artist.dto.ArtistFestivalV1Response
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import com.festago.artist.infrastructure.repository.query.ArtistDetailV1QueryDslRepository
import com.festago.artist.infrastructure.repository.query.ArtistFestivalSearchCondition
import com.festago.common.cache.CacheEvictCommandEvent
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
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
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArtistDetailV1QueryService(
    private val artistDetailV1QueryDslRepository: ArtistDetailV1QueryDslRepository,
    private val clock: Clock,
) {

    @Cacheable(cacheNames = [ARTIST_DETAIL_CACHE_NAME], key = "#artistId")
    fun findArtistDetail(artistId: Long): ArtistDetailV1Response {
        return artistDetailV1QueryDslRepository.findArtistDetail(artistId)
            ?: throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
    }

    @Cacheable(
        cacheNames = [ARTIST_DETAIL_FESTIVALS_CACHE_NAME],
        key = "#artistId",
        condition = "#lastFestivalId == null && #lastStartDate == null && #isPast == false && #pageable.pageSize == 10"
    )
    fun findArtistFestivals(
        artistId: Long,
        lastFestivalId: Long?,
        lastStartDate: LocalDate?,
        isPast: Boolean,
        pageable: Pageable,
    ): Slice<ArtistFestivalV1Response> {
        return artistDetailV1QueryDslRepository.findArtistFestivals(
            ArtistFestivalSearchCondition(
                artistId = artistId,
                isPast = isPast,
                lastFestivalId = lastFestivalId,
                lastStartDate = lastStartDate,
                pageable = pageable,
                currentTime = LocalDate.now(clock)
            )
        )
    }

    companion object {
        const val ARTIST_DETAIL_CACHE_NAME = "ARTIST_DETAIL_V1"
        const val ARTIST_DETAIL_FESTIVALS_CACHE_NAME = "ARTIST_DETAIL_FESTIVALS_V1"
    }
}

@Configuration
class ArtistDetailV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun artistDetailV1QueryServiceArtistDetailCache(): Cache {
        return CaffeineCache(
            ArtistDetailV1QueryService.ARTIST_DETAIL_CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10)
                .build()
        )
    }

    @Bean
    fun artistDetailV1QueryServiceArtistDetailFestivalsCache(): Cache {
        return CaffeineCache(
            ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10)
                .build()
        )
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
                ArtistDetailV1QueryService.ARTIST_DETAIL_CACHE_NAME,
                event.artist.identifier.toString()
            )
        )
    }

    @EventListener
    fun evictWhenArtistDeleted(event: ArtistDeletedEvent) {
        eventPublisher.publishEvent(
            CacheEvictCommandEvent(
                ArtistDetailV1QueryService.ARTIST_DETAIL_FESTIVALS_CACHE_NAME,
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
