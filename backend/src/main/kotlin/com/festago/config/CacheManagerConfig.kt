package com.festago.config

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
@EnableCaching(order = -100)
class CacheManagerConfig {

    @Bean
    fun cacheManager(caches: List<Cache>): CacheManager {
        return SimpleCacheManager().apply {
            setCaches(caches)
        }
    }
}
