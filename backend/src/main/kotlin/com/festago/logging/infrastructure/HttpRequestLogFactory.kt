package com.festago.logging.infrastructure

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.logging.domain.RequestLog
import com.festago.logging.domain.RequestLoggingPolicy
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class HttpRequestLogFactory(
    private val authenticateContext: AuthenticateContext,
) {

    fun generate(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        loggingPolicy: RequestLoggingPolicy,
        processTime: Long,
    ): RequestLog = RequestLog(
        httpMethod = request.method,
        requestUri = request.getRequestUri(),
        userId = authenticateContext.id,
        requestIp = request.getHeader("X-Forwarded-For"),
        requestContentType = request.contentType,
        requestSize = request.contentLength,
        requestBody = if (loggingPolicy.hideRequestBody) "MASKED" else getRequestBody(request),
        responseSize = response.contentSize,
        responseBody = if (loggingPolicy.hideResponseBody) "MASKED" else getResponseBody(response),
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
