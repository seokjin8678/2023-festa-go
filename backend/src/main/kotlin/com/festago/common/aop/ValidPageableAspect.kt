package com.festago.common.aop

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class ValidPageableAspect {

    @Before("@annotation(validPageable)")
    fun doValid(validPageable: ValidPageable) {
        val sizeKey = validPageable.sizeKey
        val maxSize = validPageable.maxSize

        val sizeString = getSizeString(sizeKey)
        if (sizeString.isNullOrBlank()) { // 쿼리 파라미터에 size가 없을 경우 spring의 기본 값 사용
            return
        }
        val size = parseSize(sizeString)
        validateMaxSize(size, maxSize)
    }

    private fun getSizeString(sizeKey: String): String? {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
            ?: return null
        val request = attributes.request
        return request.getParameter(sizeKey)
    }

    private fun parseSize(size: String): Int {
        try {
            return size.toInt()
        } catch (e: NumberFormatException) {
            throw BadRequestException(ErrorCode.INVALID_NUMBER_FORMAT_PAGING_SIZE)
        }
    }

    private fun validateMaxSize(size: Int, maxSize: Int) {
        if (size < 1) {
            throw BadRequestException(ErrorCode.INVALID_NUMBER_FORMAT_PAGING_SIZE)
        }
        if (maxSize < size) {
            throw BadRequestException(ErrorCode.INVALID_PAGING_MAX_SIZE)
        }
    }
}
