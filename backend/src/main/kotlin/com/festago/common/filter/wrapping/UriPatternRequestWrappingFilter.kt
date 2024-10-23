package com.festago.common.filter.wrapping

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper

/**
 * LogRequestBodyAspect 클래스가 해당 클래스에 의존하므로, 해당 클래스 수정, 삭제 시 LogRequestBodyAspect 클래스도 수정하거나 삭제할 것!
 */
@Profile("!test")
@Component
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
            chain.doFilter(wrappingRequest, response)
        } else {
            chain.doFilter(request, response)
        }
    }
}

