package com.festago.common.annotation

annotation class LoggingDetail(
    val disable: Boolean = false,
    val hideRequestBody: Boolean = false,
    val hideResponseBody: Boolean = false,
)
