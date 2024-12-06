package com.festago.web.filter

import com.festago.web.application.RequestLoggingService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

private val log = KotlinLogging.logger {}

@Component
@Order(200)
class RequestLoggingFilter(
    private val requestLoggingService: RequestLoggingService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (request is ContentCachingRequestWrapper && response is ContentCachingResponseWrapper) {
            try {
                val start = System.currentTimeMillis()
                filterChain.doFilter(request, response)
                val processTime = System.currentTimeMillis() - start
                requestLoggingService.logging(request, response, processTime)
                response.copyBodyToResponse()
            } catch (e: Exception) {
                log.error(e) { e.message }
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }
}
