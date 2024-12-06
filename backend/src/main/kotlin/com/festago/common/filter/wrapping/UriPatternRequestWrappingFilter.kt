package com.festago.common.filter.wrapping

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Profile("!test")
@Component
@Order(100)
class UriPatternRequestWrappingFilter(
    private val uriPatternMatcher: UriPatternMatcher,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        if (uriPatternMatcher.match(RequestMethod.resolve(request.method)!!, request.requestURI)) {
            val wrappingRequest = ContentCachingRequestWrapper(request)
            val wrappingResponse = ContentCachingResponseWrapper(response)
            chain.doFilter(wrappingRequest, wrappingResponse)
        } else {
            chain.doFilter(request, response)
        }
    }
}

