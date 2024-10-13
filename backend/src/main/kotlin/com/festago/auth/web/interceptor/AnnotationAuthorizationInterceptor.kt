package com.festago.auth.web.interceptor

import com.festago.auth.annotation.Authorization
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.token.http.HttpRequestTokenExtractor
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import com.festago.common.exception.UnexpectedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.util.Assert
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

class AnnotationAuthorizationInterceptor(
    private val httpRequestTokenExtractor: HttpRequestTokenExtractor,
    private val authenticationTokenExtractor: AuthenticationTokenExtractor,
    private val authenticateContext: AuthenticateContext,
) : HandlerInterceptor {

    init {
        Assert.notNull(httpRequestTokenExtractor, "The httpRequestTokenExtractor must not be null")
        Assert.notNull(authenticationTokenExtractor, "The authenticationTokenExtractor must not be null")
        Assert.notNull(authenticateContext, "The authenticateContext must not be null")
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as HandlerMethod
        val authorization = handlerMethod.getMethodAnnotation(Authorization::class.java)
            ?: throw UnexpectedException("HandlerMethod에 Authorization 어노테이션이 없습니다.")
        val token: String = httpRequestTokenExtractor.extract(request)
            ?: throw UnauthorizedException(ErrorCode.NEED_AUTH_TOKEN)

        val authentication = authenticationTokenExtractor.extract(token)
        if (authentication.role != authorization.role) {
            throw ForbiddenException(ErrorCode.NOT_ENOUGH_PERMISSION)
        }

        authenticateContext.authentication = authentication
        return true
    }
}
