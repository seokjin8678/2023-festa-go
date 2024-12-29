package com.festago.common.cache

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Profile("!test")
@Component
internal class CacheInvalidator(
    private val cacheManager: CacheManager,
) {

    protected fun invalidate(cacheName: String) {
        val cache = cacheManager.getCache(cacheName) ?: return
        cache.invalidate()
        log.info { "Invalidating cache complete. cacheName=$cacheName" }
    }

    @Async
    @EventListener
    protected fun invalidate(event: CacheInvalidateCommandEvent) {
        invalidate(event.cacheName)
    }
}

data class CacheInvalidateCommandEvent(val cacheName: String)

