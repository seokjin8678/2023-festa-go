package com.festago.auth.infrastructure.token.http

import com.festago.auth.domain.token.http.HttpRequestTokenExtractor
import jakarta.servlet.http.HttpServletRequest

internal class CompositeHttpRequestTokenExtractor(
    private val httpRequestTokenExtractors: List<HttpRequestTokenExtractor>,
) : HttpRequestTokenExtractor {

    override fun extract(request: HttpServletRequest): String? {
        for (httpRequestTokenExtractor in httpRequestTokenExtractors) {
            val token = httpRequestTokenExtractor.extract(request)
            if (token != null) {
                return token
            }
        }
        return null
    }
}
