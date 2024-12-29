package com.festago.festival.application.query

import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.common.exception.ValidException
import com.festago.festival.dto.FestivalV1QueryRequest
import com.festago.festival.dto.FestivalV1Response
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.festival.infrastructure.repository.query.FestivalSearchCondition
import com.festago.festival.infrastructure.repository.query.FestivalV1QueryDslRepository
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
class FestivalV1QueryService(
    private val festivalV1QueryDslRepository: FestivalV1QueryDslRepository,
    private val clock: Clock,
) {

    @Cacheable(
        cacheNames = [CACHE_NAME],
        condition = "#request.lastFestivalId == null || #request.lastStartDate == null",
        key = "#request.filter.name() + #request.location.name() + #pageable.pageSize"
    )
    fun findFestivals(pageable: Pageable, request: FestivalV1QueryRequest): Slice<FestivalV1Response> {
        validateCursor(request.lastFestivalId, request.lastStartDate)
        return festivalV1QueryDslRepository.findBy(
            FestivalSearchCondition(
                request.filter,
                request.location,
                request.lastStartDate,
                request.lastFestivalId,
                pageable,
                LocalDate.now(clock)
            )
        )
    }

    private fun validateCursor(lastFestivalId: Long?, lastStartDate: LocalDate?) {
        if (lastFestivalId == null && lastStartDate == null) {
            return
        }
        if (lastFestivalId != null && lastStartDate != null) {
            return
        }
        throw ValidException("festivalId, lastStartDate 두 값 모두 요청하거나 요청하지 않아야합니다.")
    }

    companion object {
        const val CACHE_NAME: String = "FIND_FESTIVALS_V1"
    }
}

@Configuration
class FestivalV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun festivalV1QueryServiceCache(): Cache {
        return CaffeineCache(
            FestivalV1QueryService.CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10)
                .build()
        )
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

