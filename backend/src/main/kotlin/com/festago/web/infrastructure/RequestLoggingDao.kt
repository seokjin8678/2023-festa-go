package com.festago.web.infrastructure

import com.festago.web.domain.RequestLog
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class RequestLoggingDao(
    private val jdbcTemplate: JdbcTemplate,
) {

    fun saveAll(requestLogs: List<RequestLog>) {
        jdbcTemplate.batchUpdate(SQL, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val requestLog = requestLogs[i]
                ps.setString(1, requestLog.httpMethod)
                if (requestLog.userId == null) ps.setNull(2, Types.BIGINT) else ps.setLong(2, requestLog.userId)
                ps.setString(3, requestLog.role)
                ps.setString(4, URLDecoder.decode(requestLog.requestUri, StandardCharsets.UTF_8).cut(100))
                ps.setString(5, requestLog.requestIp)
                ps.setInt(6, requestLog.requestSize)
                ps.setString(7, requestLog.requestContentType)
                ps.setString(8, requestLog.requestBody)
                ps.setInt(9, requestLog.responseSize)
                ps.setString(10, requestLog.responseContentType)
                ps.setString(11, requestLog.responseBody)
                ps.setInt(12, requestLog.processTime.toInt())
                ps.setTimestamp(13, Timestamp.valueOf(requestLog.createdAt))
            }

            override fun getBatchSize(): Int {
                return requestLogs.size
            }
        })
    }

    private fun String.cut(maxLength: Int): String {
        if (this.length > maxLength) {
            return this.substring(0, maxLength)
        }
        return this
    }

    companion object {
        const val SQL =
            "INSERT INTO request_log (method, user_id, role, request_uri, request_ip, request_size, request_content_type, request_body, response_size, response_content_type, response_body, process_time, created_at)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    }
}
