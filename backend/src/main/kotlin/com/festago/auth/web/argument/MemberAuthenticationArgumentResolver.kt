package com.festago.auth.web.argument

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.common.exception.UnexpectedException
import org.springframework.core.MethodParameter
import org.springframework.util.Assert
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class MemberAuthenticationArgumentResolver(
    private val authenticateContext: AuthenticateContext,
) : HandlerMethodArgumentResolver {

    init {
        Assert.notNull(authenticateContext, "The authenticateContext must not be null")
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == MemberAuthentication::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): MemberAuthentication {
        val authentication = authenticateContext.authentication
        if (authentication !is MemberAuthentication) {
            throw UnexpectedException("인가된 권한이 인자의 권한과 맞지 않습니다.")
        }
        return authentication
    }
}