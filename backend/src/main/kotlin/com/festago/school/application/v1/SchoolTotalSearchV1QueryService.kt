package com.festago.school.application.v1

import com.festago.common.cache.CacheInvalidateCommandEvent
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.school.dto.event.SchoolCreatedEvent
import com.festago.school.dto.event.SchoolDeletedEvent
import com.festago.school.dto.event.SchoolUpdatedEvent
import com.festago.school.dto.v1.SchoolSearchV1Response
import com.festago.school.dto.v1.SchoolTotalSearchV1Response
import com.github.benmanes.caffeine.cache.Caffeine
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
class SchoolTotalSearchV1QueryService(
    private val schoolSearchV1QueryService: SchoolSearchV1QueryService,
    private val schoolUpcomingFestivalStartDateV1QueryService: SchoolUpcomingFestivalStartDateV1QueryService,
) {

    @Cacheable(cacheNames = [CACHE_NAME], key = "#keyword")
    fun searchSchools(keyword: String): List<SchoolTotalSearchV1Response> {
        val schoolSearchResponses = schoolSearchV1QueryService.searchSchools(keyword)
        val schoolIds = schoolSearchResponses.map { it.id }
        val schoolIdToUpcomingFestivalStartDate = getSchoolIdToUpcomingFestivalStartDate(schoolIds)
        return schoolSearchResponses.map { schoolSearchResponse: SchoolSearchV1Response ->
            SchoolTotalSearchV1Response(
                schoolSearchResponse.id,
                schoolSearchResponse.name,
                schoolSearchResponse.logoUrl,
                schoolIdToUpcomingFestivalStartDate[schoolSearchResponse.id]
            )
        }
    }

    private fun getSchoolIdToUpcomingFestivalStartDate(schoolIds: List<Long>): Map<Long, LocalDate> {
        return schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(schoolIds)
    }

    companion object {
        const val CACHE_NAME = "SCHOOL_TOTAL_SEARCH_V1"
    }
}

@Configuration
class SchoolTotalSearchV1QueryServiceCacheConfig(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Bean
    fun schoolTotalSearchV1QueryServiceCache(): Cache {
        return CaffeineCache(
            SchoolTotalSearchV1QueryService.CACHE_NAME, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10)
                .build()
        )
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
