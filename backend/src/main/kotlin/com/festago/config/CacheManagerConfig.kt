package com.festago.config

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope

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

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    fun cacheBuilder(): CaffeineCacheDsl {
        return CaffeineCacheDsl()
    }
}

object CaffeineCacheBuilder {

    fun build(name: String, dsl: CaffeineCacheDsl.() -> Unit = {}): CaffeineCache {
        val (recordStat, expireAfterWrite, maximumSize) = CaffeineCacheDsl().apply(dsl)
        return CaffeineCache(name, Caffeine.newBuilder().apply {
            if (recordStat) {
                recordStats()
            }
            expireAfterWrite(expireAfterWrite)
            maximumSize(maximumSize)
        }.build())
    }
}

data class CaffeineCacheDsl(
    var recordStat: Boolean = true,
    var expireAfterWrite: Duration = Duration.ofHours(1),
    var maximumSize: Long = 10,
)
