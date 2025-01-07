package com.festago.common.cache.infrastructure

import com.festago.common.cache.CacheBuilder
import com.festago.common.cache.CacheFactory
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
private class CaffeineCacheFactory : CacheFactory {

    override fun create(name: String, builder: CacheBuilder.() -> Unit): Cache {
        val (recordStat, expireAfterWrite, maximumSize) = CacheBuilder().apply(builder)
        return CaffeineCache(name, Caffeine.newBuilder().apply {
            if (recordStat) {
                recordStats()
            }
            expireAfterWrite(expireAfterWrite)
            maximumSize(maximumSize)
        }.build())
    }
}
