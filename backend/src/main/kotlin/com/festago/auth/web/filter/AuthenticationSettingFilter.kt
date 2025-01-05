package com.festago.auth.web.filter

import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.token.http.HttpRequestTokenExtractor
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class AuthenticationSettingFilter(
    private val authenticateContext: AuthenticateContext,
    private val httpRequestTokenExtractor: HttpRequestTokenExtractor,
    private val authenticationTokenExtractor: AuthenticationTokenExtractor,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = httpRequestTokenExtractor.extract(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }
        val authentication = try {
            authenticationTokenExtractor.extract(token)
        } catch (e: Exception) {
            AnonymousAuthentication
        }
        authenticateContext.authentication = authentication
        filterChain.doFilter(request, response)
    }
}
