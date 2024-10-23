package com.festago.common.exception

import org.springframework.core.NestedRuntimeException

abstract class FestaGoException : NestedRuntimeException {
    val errorCode: ErrorCode

    protected constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
    }

    protected constructor(errorCode: ErrorCode, cause: Throwable) : super(errorCode.message, cause) {
        this.errorCode = errorCode
    }

    protected constructor(errorCode: ErrorCode, message: String) : super(message) {
        this.errorCode = errorCode
    }
}
