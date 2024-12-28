package com.festago.logging.filter

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.logging.application.RequestLoggingService
import com.festago.logging.domain.RequestLog
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

private val log = KotlinLogging.logger {}

@Component
@Order(200)
class RequestLoggingFilter(
    private val requestLoggingService: RequestLoggingService,
    private val authenticateContext: AuthenticateContext,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (request is ContentCachingRequestWrapper && response is ContentCachingResponseWrapper) {
            val start = System.currentTimeMillis()
            try {
                filterChain.doFilter(request, response)
            } finally {
                val processTime = System.currentTimeMillis() - start
                try {
                    val requestLog = createRequestLog(request, response, processTime)
                    requestLoggingService.logging(requestLog)
                    response.copyBodyToResponse()
                } catch (e: Exception) {
                    log.error(e) { e.message }
                }
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }

    private fun createRequestLog(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        processTime: Long,
    ): RequestLog = RequestLog(
        httpMethod = request.method,
        requestUri = request.getRequestUri(),
        userId = authenticateContext.id,
        requestIp = request.getHeader("X-Forwarded-For"),
        requestContentType = request.contentType,
        requestSize = request.contentLength,
        requestBody = getRequestBody(request),
        responseSize = response.contentSize,
        responseBody = getResponseBody(response),
        responseContentType = response.contentType,
        processTime = processTime.toInt(),
        createdAt = LocalDateTime.now(),
    )

    private fun ContentCachingRequestWrapper.getRequestUri(): String {
        val queryString = queryString ?: return requestURI
        return requestURI + "?" + URLDecoder.decode(queryString, StandardCharsets.UTF_8)
    }

    private fun getRequestBody(request: ContentCachingRequestWrapper): String? {
        val requestSize = request.contentLength
        val contentType = request.contentType ?: return null
        if (requestSize <= 0 || contentType.startsWith(APPLICATION_JSON_VALUE).not()) {
            return null
        }
        return if (requestSize > 1000) {
            String(request.contentAsByteArray, 0, 1000, StandardCharsets.UTF_8)
        } else {
            String(request.contentAsByteArray, StandardCharsets.UTF_8)
        }
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String? {
        val responseSize = response.contentSize
        if (responseSize <= 0 || response.contentType.startsWith(APPLICATION_JSON_VALUE).not()) {
            return null
        }
        return if (responseSize > 1000) {
            String(response.contentAsByteArray, 0, 1000, StandardCharsets.UTF_8)
        } else {
            String(response.contentAsByteArray, StandardCharsets.UTF_8)
        }
    }
}
