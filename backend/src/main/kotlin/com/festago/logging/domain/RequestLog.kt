package com.festago.logging.domain

import java.time.LocalDateTime

data class RequestLog(
    val httpMethod: String?,
    val requestUri: String?,
    val userId: Long?,
    val requestIp: String?,
    val requestContentType: String?,
    val requestSize: Int,
    val requestBody: String?,
    val responseSize: Int,
    val responseStatus: Int,
    val responseBody: String?,
    val responseContentType: String?,
    val processTime: Int,
    val createdAt: LocalDateTime,
)
