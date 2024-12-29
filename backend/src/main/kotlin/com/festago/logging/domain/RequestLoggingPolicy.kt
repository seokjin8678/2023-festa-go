package com.festago.logging.domain

data class RequestLoggingPolicy(
    val disable: Boolean = false,
    val hideRequestBody: Boolean = false,
    val hideResponseBody: Boolean = false,
) {
    companion object {
        val DEFAULT = RequestLoggingPolicy()
    }
}
