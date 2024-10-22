package com.festago.auth.domain.token.http

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

internal class HeaderHttpRequestTokenExtractor : HttpRequestTokenExtractor {

    override fun extract(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return extractToken(header)
    }

    private fun extractToken(header: String): String {
        validateHeader(header)
        return header.substring(BEARER_TOKEN_PREFIX.length)
    }

    private fun validateHeader(header: String) {
        if (!header.startsWith(BEARER_TOKEN_PREFIX)) {
            throw UnauthorizedException(ErrorCode.NOT_BEARER_TOKEN_TYPE)
        }
    }

    companion object {
        private const val BEARER_TOKEN_PREFIX = "Bearer "
    }
}
