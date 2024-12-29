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
internal class CacheManageEventListener(
    private val cacheManager: CacheManager,
) {

    @Async
    @EventListener
    protected fun invalidate(event: CacheInvalidateCommandEvent) {
        val cacheName = event.cacheName
        val cache = cacheManager.getCache(cacheName) ?: return
        cache.invalidate()
        log.info { "Invalidating cache complete. cacheName=$cacheName" }
    }

    @Async
    @EventListener
    protected fun evict(event: CacheEvictCommandEvent) {
        val cacheName = event.cacheName
        val cache = cacheManager.getCache(cacheName) ?: return
        cache.evict(event.key)
    }
}

data class CacheInvalidateCommandEvent(val cacheName: String)

data class CacheEvictCommandEvent(val cacheName: String, val key: String)
