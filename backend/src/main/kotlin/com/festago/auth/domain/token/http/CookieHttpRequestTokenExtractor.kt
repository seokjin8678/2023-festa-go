package com.festago.auth.domain.token.http

import jakarta.servlet.http.HttpServletRequest

internal class CookieHttpRequestTokenExtractor : HttpRequestTokenExtractor {

    override fun extract(request: HttpServletRequest): String? {
        for (cookie in request.cookies ?: return null) {
            if (TOKEN == cookie.name) {
                return cookie.value
            }
        }
        return null
    }

    companion object {
        private const val TOKEN = "token"
    }
}
