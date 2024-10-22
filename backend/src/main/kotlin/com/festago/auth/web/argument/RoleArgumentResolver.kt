package com.festago.auth.web.argument

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.common.exception.UnexpectedException
import org.springframework.core.MethodParameter
import org.springframework.util.Assert
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Deprecated("기존 Long으로 식별자를 받는 Controller가 많기에, 해당 클래스 삭제하지 않고 유지")
class RoleArgumentResolver(
    private val role: Role,
    private val authenticateContext: AuthenticateContext,
) : HandlerMethodArgumentResolver {

    init {
        Assert.notNull(authenticateContext, "The authenticateContext must not be null")
        Assert.notNull(role, "The role must not be null")
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Long::class.java && parameter.hasParameterAnnotation(role.annotation.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long? {
        if (authenticateContext.role != this.role) {
            throw UnexpectedException("인가된 권한이 인자의 권한과 맞지 않습니다.")
        }
        return authenticateContext.id
    }
}
