package com.festago.common.exception.dto

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.FestaGoException
import com.festago.common.exception.ValidException

data class ErrorResponse(
    val errorCode: ErrorCode,
    val message: String,
) {
    companion object {

        fun from(festaGoException: FestaGoException): ErrorResponse {
            return from(festaGoException.errorCode)
        }

        fun from(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(errorCode, errorCode.message)
        }

        fun from(e: ValidException): ErrorResponse {
            return ErrorResponse(e.errorCode, e.message ?: "")
        }
    }
}
