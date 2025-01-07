package com.festago.common.cache.infrastructure

import com.festago.common.cache.CacheBuilder
import com.festago.common.cache.CacheFactory
import org.springframework.cache.Cache
import org.springframework.cache.support.NoOpCache
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("test")
@Component
private class NoOpCacheFactory : CacheFactory {

    override fun create(name: String, builder: CacheBuilder.() -> Unit): Cache {
        return NoOpCache(name)
    }
}
