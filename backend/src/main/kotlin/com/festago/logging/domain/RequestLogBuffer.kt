package com.festago.logging.domain

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.locks.ReentrantLock

class RequestLogBuffer(
    capacity: Int = 100,
) {

    private val buffer = ArrayBlockingQueue<RequestLog>(capacity)
    private val lock = ReentrantLock()

    fun offerLog(requestLog: RequestLog): Boolean {
        return buffer.offer(requestLog)
    }

    fun drainLogs(requestLogs: MutableList<RequestLog>) {
        if (lock.tryLock()) {
            try {
                buffer.drainTo(requestLogs)
            } finally {
                lock.unlock()
            }
        }
    }
}
