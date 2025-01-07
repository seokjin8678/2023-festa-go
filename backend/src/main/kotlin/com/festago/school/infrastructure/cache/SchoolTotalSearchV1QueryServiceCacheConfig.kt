package com.festago.school.infrastructure.cache

import com.festago.common.cache.CacheFactory
import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.school.application.v1.SchoolTotalSearchV1QueryService
import com.festago.school.dto.event.SchoolCreatedEvent
import com.festago.school.dto.event.SchoolDeletedEvent
import com.festago.school.dto.event.SchoolUpdatedEvent
import org.springframework.cache.Cache
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled

@Configuration
private class SchoolTotalSearchV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
    private val cacheFactory: CacheFactory,
) {

    @Bean
    fun schoolTotalSearchV1QueryServiceCache(): Cache {
        return cacheFactory.create(SchoolTotalSearchV1QueryService.CACHE_NAME)
    }

    @EventListener(value = [SchoolCreatedEvent::class, SchoolUpdatedEvent::class, SchoolDeletedEvent::class])
    fun invalidateCacheWhenSchoolModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(SchoolTotalSearchV1QueryService.CACHE_NAME))
    }

    @EventListener(value = [FestivalCreatedEvent::class, FestivalUpdatedEvent::class, FestivalDeletedEvent::class])
    fun invalidateCacheWhenFestivalModified() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(SchoolTotalSearchV1QueryService.CACHE_NAME))
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun invalidateCacheAtMidnight() {
        eventPublisher.publishEvent(CacheInvalidateCommandEvent(SchoolTotalSearchV1QueryService.CACHE_NAME))
    }
}
