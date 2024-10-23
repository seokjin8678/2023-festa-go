package com.festago.auth.web.argument

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.authentication.Authentication
import org.springframework.core.MethodParameter
import org.springframework.util.Assert
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AuthenticationArgumentResolver(
    private val authenticateContext: AuthenticateContext,
) : HandlerMethodArgumentResolver {

    init {
        Assert.notNull(authenticateContext, "The authenticateContext must not be null")
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Authentication::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Authentication {
        return authenticateContext.authentication
    }
}
