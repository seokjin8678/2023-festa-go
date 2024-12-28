package com.festago.logging.infrastructure.pattern

import com.festago.logging.domain.RequestLoggingPolicy
import com.festago.logging.domain.RequestLoggingUriPatternMatcher
import org.springframework.util.AntPathMatcher

@Deprecated("새로운 PatternMatcher의 성능 비교용으로 삭제하지 않고 유지")
internal class AntRequestLoggingUriPatternMatcher : RequestLoggingUriPatternMatcher {

    private val antPathMatcher = AntPathMatcher()
    private val methodToPatterns: MutableMap<String, MutableMap<String, RequestLoggingPolicy>> = HashMap()

    override fun addPattern(method: String, path: String, policy: RequestLoggingPolicy) {
        val patterns = methodToPatterns.computeIfAbsent(method) { HashMap() }
        patterns[path] = policy
    }

    override fun match(method: String, path: String): RequestLoggingPolicy? {
        val patterns = methodToPatterns[method] ?: return null
        for ((pattern, policy) in patterns) {
            if (antPathMatcher.match(pattern, path)) {
                return policy
            }
        }
        return null
    }
}
