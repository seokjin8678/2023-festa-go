package com.festago.web.application

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.web.domain.RequestLog
import com.festago.web.infrastructure.RequestLoggingDao
import jakarta.annotation.PreDestroy
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import org.springframework.core.task.TaskExecutor
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Service
class RequestLoggingService(
    private val taskExecutor: TaskExecutor,
    private val requestLoggingDao: RequestLoggingDao,
    private val authenticateContext: AuthenticateContext,
) {

    private val buffer = ArrayBlockingQueue<RequestLog>(10)
    private val lock = ReentrantLock()

    @PreDestroy
    protected fun preDestroy() {
        saveLogsFromBuffer()
    }

    private fun saveLogsFromBuffer(requestLogs: MutableList<RequestLog> = mutableListOf()) {
        if (lock.tryLock()) {
            try {
                buffer.drainTo(requestLogs)
            } finally {
                lock.unlock()
            }
        }
        if (requestLogs.isEmpty()) {
            return
        }
        taskExecutor.execute { requestLoggingDao.saveAll(requestLogs) }
    }

    @Scheduled(initialDelay = 60, fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
    protected fun scheduleRequestLog() {
        saveLogsFromBuffer()
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
        if (!buffer.offer(requestLog)) {
            saveLogsFromBuffer(mutableListOf(requestLog))
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
