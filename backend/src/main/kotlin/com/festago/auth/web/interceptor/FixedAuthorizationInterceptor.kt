package com.festago.auth.web.interceptor

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.token.http.HttpRequestTokenExtractor
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.util.Assert
import org.springframework.web.servlet.HandlerInterceptor

class FixedAuthorizationInterceptor(
    private val httpRequestTokenExtractor: HttpRequestTokenExtractor,
    private val authenticationTokenExtractor: AuthenticationTokenExtractor,
    private val authenticateContext: AuthenticateContext,
    private val role: Role,
) : HandlerInterceptor {

    init {
        Assert.notNull(httpRequestTokenExtractor, "The httpRequestTokenExtractor must not be null")
        Assert.notNull(authenticationTokenExtractor, "The authenticationTokenExtractor must not be null")
        Assert.notNull(authenticateContext, "The authenticateContext must not be null")
        Assert.notNull(role, "The role must not be null")
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = httpRequestTokenExtractor.extract(request)
            ?: throw UnauthorizedException(ErrorCode.NEED_AUTH_TOKEN)

        val authentication = authenticationTokenExtractor.extract(token)
        if (authentication.role != role) {
            throw ForbiddenException(ErrorCode.NOT_ENOUGH_PERMISSION)
        }

        authenticateContext.authentication = authentication
        return true
    }
}
