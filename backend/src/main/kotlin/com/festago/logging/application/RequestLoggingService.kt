package com.festago.logging.application

import com.festago.logging.domain.RequestLog
import com.festago.logging.domain.RequestLogBuffer
import com.festago.logging.infrastructure.RequestLoggingDao
import jakarta.annotation.PreDestroy
import java.util.concurrent.TimeUnit
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RequestLoggingService(
    private val taskExecutor: TaskExecutor,
    private val requestLoggingDao: RequestLoggingDao,
) {

    private val requestLogBuffer = RequestLogBuffer()

    fun logging(requestLog: RequestLog) {
        if (!requestLogBuffer.offerLog(requestLog)) {
            taskExecutor.execute { saveLogsFromBuffer(mutableListOf(requestLog)) }
        }
    }

    private fun saveLogsFromBuffer(requestLogs: MutableList<RequestLog> = mutableListOf()) {
        requestLogBuffer.drainLogs(requestLogs)
        if (requestLogs.isEmpty()) {
            return
        }
        requestLoggingDao.saveAll(requestLogs)
    }

    @Scheduled(initialDelay = 60, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    protected fun scheduleRequestLog() {
        taskExecutor.execute { saveLogsFromBuffer() }
    }

    @PreDestroy
    protected fun preDestroy() {
        saveLogsFromBuffer()
    }
}
