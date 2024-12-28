package com.festago.logging.filter

import com.festago.logging.application.RequestLoggingService
import com.festago.logging.domain.RequestLoggingUriPatternMatcher
import com.festago.logging.infrastructure.HttpRequestLogFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

private val log = KotlinLogging.logger {}

@Profile("!test")
@Component
class RequestLoggingFilter(
    private val requestLoggingService: RequestLoggingService,
    private val requestLogFactory: HttpRequestLogFactory,
    private val uriPatternMatcher: RequestLoggingUriPatternMatcher,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val loggingPolicy = uriPatternMatcher.match(request.method, request.requestURI)
            ?: return filterChain.doFilter(request, response)
        val wrappingRequest = ContentCachingRequestWrapper(request)
        val wrappingResponse = ContentCachingResponseWrapper(response)
        val start = System.currentTimeMillis()
        try {
            filterChain.doFilter(wrappingRequest, wrappingResponse)
        } finally {
            val processTime = System.currentTimeMillis() - start
            val requestLog = requestLogFactory.generate(
                request = wrappingRequest,
                response = wrappingResponse,
                loggingPolicy = loggingPolicy,
                processTime = processTime
            )
            wrappingResponse.copyBodyToResponse()
            try {
                requestLoggingService.logging(requestLog)
            } catch (e: Exception) {
                log.error(e) { e.message }
            }
        }
    }
}
