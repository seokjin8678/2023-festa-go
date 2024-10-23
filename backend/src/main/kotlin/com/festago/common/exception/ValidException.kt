package com.festago.common.exception

class ValidException(message: String) : FestaGoException(ErrorCode.VALIDATION_FAIL, message)
