package com.festago.common.aop

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.event.Level
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.ContentCachingRequestWrapper

private val log = KotlinLogging.logger {}

@Aspect
@Component
class LogRequestBodyAspect {

    @Around("@annotation(requestBody)")
    fun handleAll(pjp: ProceedingJoinPoint, requestBody: LogRequestBody): Any {
        val level = requestBody.level
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes?
            ?: return pjp.proceed()

        val request = attributes.request as? ContentCachingRequestWrapper ?: return pjp.proceed()

        if (!isLoggable(request)) {
            return pjp.proceed()
        }

        if (requestBody.exceptionOnly) {
            try {
                return pjp.proceed()
            } catch (e: Throwable) {
                log(level, request)
                throw e
            }
        }

        log(level, request)
        return pjp.proceed()
    }

    private fun isLoggable(request: HttpServletRequest): Boolean {
        val contentLength = request.contentLength
        if (contentLength <= 0 || contentLength > MAX_CONTENT_LENGTH) {
            return false
        }
        return request.contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)
    }

    private fun log(level: Level, request: ContentCachingRequestWrapper) {
        // GET /api/v1/~~
        // {"foo":"bar"}
        val body = "${request.method} ${request.requestURI}\n${request.contentAsString}"
        when (level) {
            Level.ERROR -> log.error { body }
            Level.WARN -> log.warn { body }
            Level.INFO -> log.info { body }
            else -> {} // noop
        }
    }

    companion object {
        private const val MAX_CONTENT_LENGTH = 2000
    }
}
