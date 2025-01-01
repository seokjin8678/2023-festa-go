package com.festago.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import org.springframework.cache.caffeine.CaffeineCache

fun caffeineCache(name: String, builder: CaffeineCacheBuilder.() -> Unit = {}): CaffeineCache {
    val (recordStat, expireAfterWrite, maximumSize) = CaffeineCacheBuilder().apply(builder)
    return CaffeineCache(name, Caffeine.newBuilder().apply {
        if (recordStat) {
            recordStats()
        }
        expireAfterWrite(expireAfterWrite)
        maximumSize(maximumSize)
    }.build())
}

data class CaffeineCacheBuilder(
    var recordStat: Boolean = true,
    var expireAfterWrite: Duration = Duration.ofHours(1),
    var maximumSize: Long = 10,
)
