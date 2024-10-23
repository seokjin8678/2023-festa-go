package com.festago.common.exception

class UnexpectedException(message: String) : FestaGoException(ErrorCode.INTERNAL_SERVER_ERROR, message)
