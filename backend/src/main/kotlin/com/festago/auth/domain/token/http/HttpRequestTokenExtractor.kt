package com.festago.auth.domain.token.http

import jakarta.servlet.http.HttpServletRequest

fun interface HttpRequestTokenExtractor {
    fun extract(request: HttpServletRequest): String?
}
