package com.festago.logging.infrastructure

import com.festago.logging.domain.RequestLog
import java.sql.Timestamp
import java.sql.Types
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class RequestLoggingDao(
    private val jdbcTemplate: JdbcTemplate,
) {

    fun saveAll(requestLogs: List<RequestLog>) {
        if (requestLogs.isEmpty()) {
            return
        }
        jdbcTemplate.batchUpdate(SQL, requestLogs, BATCH_SIZE) { ps, requestLog ->
            ps.setString(1, requestLog.httpMethod)
            if (requestLog.userId == null) ps.setNull(2, Types.BIGINT) else ps.setLong(2, requestLog.userId)
            ps.setString(3, requestLog.requestUri?.take(100))
            ps.setString(4, requestLog.requestIp?.take(15))
            ps.setInt(5, requestLog.requestSize)
            ps.setString(6, requestLog.requestContentType?.take(100))
            ps.setString(7, requestLog.requestBody)
            ps.setInt(8, requestLog.responseSize)
            ps.setString(9, requestLog.responseContentType?.take(50))
            ps.setString(10, requestLog.responseBody)
            ps.setInt(11, requestLog.processTime)
            ps.setTimestamp(12, Timestamp.valueOf(requestLog.createdAt))
        }
    }

    companion object {
        const val BATCH_SIZE = 100
        const val SQL =
            "INSERT INTO request_log (method, user_id, request_uri, request_ip, request_size, request_content_type, request_body, response_size, response_content_type, response_body, process_time, created_at)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    }
}
