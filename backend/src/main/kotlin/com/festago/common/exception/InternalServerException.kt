package com.festago.common.exception

class InternalServerException : FestaGoException {
    constructor(errorCode: ErrorCode) : super(errorCode)

    constructor(errorCode: ErrorCode, cause: Throwable) : super(errorCode, cause)
}
