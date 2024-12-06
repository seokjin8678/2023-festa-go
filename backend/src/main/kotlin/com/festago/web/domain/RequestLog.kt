package com.festago.web.domain

import java.time.LocalDateTime

data class RequestLog(
    val httpMethod: String?,
    val requestUri: String?,
    val userId: Long?,
    val role: String?,
    val requestIp: String?,
    val requestContentType: String?,
    val requestSize: Int,
    val requestBody: String?,
    val responseSize: Int,
    val responseBody: String?,
    val responseContentType: String?,
    val processTime: Long,
    val createdAt: LocalDateTime,
)
