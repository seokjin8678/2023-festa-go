package com.festago.common.exception.dto

import com.festago.common.exception.ErrorCode
import org.springframework.web.bind.MethodArgumentNotValidException

data class ValidErrorResponse(
    val errorCode: ErrorCode,
    val message: String,
    val result: Map<String, String>,
) {
    companion object {
        fun from(e: MethodArgumentNotValidException): ValidErrorResponse {
            return ValidErrorResponse(
                errorCode = ErrorCode.INVALID_REQUEST_ARGUMENT,
                message = ErrorCode.INVALID_REQUEST_ARGUMENT.message,
                result = e.bindingResult.fieldErrors.associateBy({ it.field }, { it.defaultMessage ?: "잘못된 요청입니다." })
            )
        }
    }
}
