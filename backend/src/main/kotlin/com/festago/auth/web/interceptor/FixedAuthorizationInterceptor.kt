package com.festago.auth.web.interceptor

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class FixedAuthorizationInterceptor(
    private val authenticateContext: AuthenticateContext,
    private val role: Role,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authentication = authenticateContext.authentication
        if (authentication == AnonymousAuthentication) {
            throw UnauthorizedException(ErrorCode.NEED_AUTH_TOKEN)
        }
        if (authentication.role != role) {
            throw ForbiddenException(ErrorCode.NOT_ENOUGH_PERMISSION)
        }
        return true
    }
}
