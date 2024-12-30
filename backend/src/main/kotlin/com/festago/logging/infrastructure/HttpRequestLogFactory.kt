package com.festago.logging.infrastructure

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.logging.domain.RequestLog
import com.festago.logging.domain.RequestLoggingPolicy
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        loggingPolicy: RequestLoggingPolicy,
        processTime: Long,
    ): RequestLog = RequestLog(
        httpMethod = request.method,
        requestUri = request.getRequestUri(),
        userId = authenticateContext.id,
        requestIp = request.getHeader("X-Forwarded-For"),
        requestContentType = request.contentType,
        requestSize = request.contentLength,
        requestBody = if (loggingPolicy.hideRequestBody) "MASKED" else getRequestBody(request as ContentCachingRequestWrapper),
        responseSize = if (loggingPolicy.hideResponseBody) -1 else getResponseSize(response as ContentCachingResponseWrapper),
        responseBody = if (loggingPolicy.hideResponseBody) "MASKED" else getResponseBody(response as ContentCachingResponseWrapper),
        responseContentType = response.contentType,
        responseStatus = response.status,
        processTime = processTime.toInt(),
        createdAt = LocalDateTime.now(),
    )

    private fun HttpServletRequest.getRequestUri(): String {
        val queryString = queryString ?: return requestURI
        return requestURI + "?" + URLDecoder.decode(queryString, StandardCharsets.UTF_8)
    }

    private fun getRequestBody(request: ContentCachingRequestWrapper): String? {
        val requestSize = request.contentLength
        val contentType = request.contentType ?: return null
        if (requestSize <= 0 || contentType != APPLICATION_JSON_VALUE) {
            return null
        }
        return if (requestSize > 1000) {
            String(request.contentAsByteArray, 0, 1000, StandardCharsets.UTF_8)
        } else {
            String(request.contentAsByteArray, StandardCharsets.UTF_8)
        }
    }

    private fun getResponseSize(response: ContentCachingResponseWrapper): Int {
        return response.contentSize
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String? {
        val responseSize = response.contentSize
        if (responseSize <= 0 || response.contentType != APPLICATION_JSON_VALUE) {
            return null
        }
        val responseBody = if (responseSize > 1000) {
            String(response.contentAsByteArray, 0, 1000, StandardCharsets.UTF_8)
        } else {
            String(response.contentAsByteArray, StandardCharsets.UTF_8)
        }
        response.copyBodyToResponse()
        return responseBody
    }
}
