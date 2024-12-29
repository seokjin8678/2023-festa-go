package com.festago.festival.application.query

import com.festago.common.cache.CacheEvictCommandEvent
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.dto.FestivalDetailV1Response
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.festival.infrastructure.repository.query.FestivalDetailV1QueryDslRepository
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
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FestivalDetailV1QueryService(
    private val festivalDetailV1QueryDslRepository: FestivalDetailV1QueryDslRepository,
) {

    @Cacheable(cacheNames = [CACHE_NAME], key = "#festivalId")
    fun findFestivalDetail(festivalId: Long): FestivalDetailV1Response {
        return festivalDetailV1QueryDslRepository.findFestivalDetail(festivalId)
            ?: throw NotFoundException(ErrorCode.FESTIVAL_NOT_FOUND)
    }

    companion object {
        const val CACHE_NAME = "FESTIVAL_DETAIL_V1"
    }
}

@Configuration
class FestivalDetailV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun festivalDetailV1QueryServiceCache(): Cache {
        return CaffeineCache(
            FestivalDetailV1QueryService.CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(20)
                .build()
        )
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
