package com.festago.common.exception

class BadRequestException(errorCode: ErrorCode) : FestaGoException(errorCode)
