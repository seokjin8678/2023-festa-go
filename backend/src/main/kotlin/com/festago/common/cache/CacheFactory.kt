package com.festago.common.cache

import java.time.Duration
import org.springframework.cache.Cache

interface CacheFactory {

    fun create(name: String, builder: CacheBuilder.() -> Unit = {}): Cache
}

data class CacheBuilder(
    var recordStats: Boolean = true,
    var expireAfterWrite: Duration = Duration.ofHours(1),
    var maximumSize: Long = 10,
)
