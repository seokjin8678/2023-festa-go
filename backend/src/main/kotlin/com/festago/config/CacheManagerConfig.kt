package com.festago.config

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching(order = -100)
class CacheManagerConfig {

    @Bean
    fun cacheManager(caches: List<Cache>): CacheManager {
        validateDuplicateCaches(caches)
        return SimpleCacheManager().apply {
            setCaches(caches)
        }
    }

    private fun validateDuplicateCaches(caches: List<Cache>) {
        val duplicateCacheNames = caches
            .groupingBy { it.name }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
            .toList()
        if (duplicateCacheNames.isNotEmpty()) {
            throw IllegalStateException("중복된 캐시가 존재합니다. duplicateCacheNames=${duplicateCacheNames}")
        }
    }
}
