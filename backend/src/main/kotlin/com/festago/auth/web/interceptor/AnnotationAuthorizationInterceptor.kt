package com.festago.auth.web.interceptor

import com.festago.auth.annotation.Authorization
import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import com.festago.common.exception.UnexpectedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

class AnnotationAuthorizationInterceptor(
    private val authenticateContext: AuthenticateContext,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as HandlerMethod
        val authorization = handlerMethod.getMethodAnnotation(Authorization::class.java)
            ?: throw UnexpectedException("HandlerMethod에 Authorization 어노테이션이 없습니다.")

        val authentication = authenticateContext.authentication
        if (authentication == AnonymousAuthentication) {
            if (authorization.allowAnonymous()) {
                return true
            } else {
                throw UnauthorizedException(ErrorCode.NEED_AUTH_TOKEN)
            }
        }
        if (authentication.role !in authorization.allowRoles) {
            throw ForbiddenException(ErrorCode.NOT_ENOUGH_PERMISSION)
        }
        return true
    }

    private fun Authorization.allowAnonymous(): Boolean {
        return Role.ANONYMOUS in allowRoles
    }
}
