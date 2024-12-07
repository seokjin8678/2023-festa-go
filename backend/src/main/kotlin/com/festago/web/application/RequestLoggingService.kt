package com.festago.web.application

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.web.domain.RequestLog
import com.festago.web.infrastructure.RequestLoggingDao
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PreDestroy
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import org.springframework.core.task.TaskExecutor
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

private val log = KotlinLogging.logger {}

@Service
class RequestLoggingService(
    private val taskExecutor: TaskExecutor,
    private val requestLoggingDao: RequestLoggingDao,
    private val authenticateContext: AuthenticateContext,
) {

    private val buffer = mutableListOf<RequestLog>()
    private val lock = ReentrantLock()

    @PreDestroy
    protected fun preDestroy() {
        if (buffer.isEmpty()) {
            return
        }
        lock.lock()
        try {
            requestLoggingDao.saveAll(buffer)
            log.info { "requestLog 저장 완료" }
        } catch (e: Exception) {
            log.error(e) { e.message }
        } finally {
            lock.unlock()
        }
    }

    @Scheduled(initialDelay = 60, fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
    protected fun scheduleRequestLog() {
        if (buffer.isEmpty()) {
            return
        }
        lock.lock()
        try {
            val requestLogs = ArrayList(buffer)
            taskExecutor.execute { requestLoggingDao.saveAll(requestLogs) }
            buffer.clear()
            log.info { "requestLog 저장 완료" }
        } finally {
            lock.unlock()
        }
    }

    fun logging(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper, processTime: Long) {
        val httpMethod = request.method
        val queryString = request.queryString
        val requestUri = request.requestURI + if (queryString != null) "?$queryString" else ""
        val requestIp = request.getHeader("X-Forwarded-For")
        val requestSize = request.contentLength
        val requestContentType = request.contentType
        val requestBody = getRequestBody(request)

        val responseSize = response.contentSize
        val responseBody = getResponseBody(response)
        val responseContentType = response.contentType

        val requestLog = RequestLog(
            httpMethod = httpMethod,
            requestUri = requestUri,
            userId = authenticateContext.id,
            role = authenticateContext.role.name,
            requestIp = requestIp,
            requestContentType = requestContentType,
            requestSize = requestSize,
            requestBody = requestBody,
            responseSize = responseSize,
            responseBody = responseBody,
            responseContentType = responseContentType,
            processTime = processTime,
            createdAt = LocalDateTime.now(),
        )
        lock.lock()
        try {
            buffer.add(requestLog)
            if (buffer.size >= 10) {
                val requestLogs = ArrayList(buffer)
                taskExecutor.execute { requestLoggingDao.saveAll(requestLogs) }
                buffer.clear()
            }
        } finally {
            lock.unlock()
        }
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
