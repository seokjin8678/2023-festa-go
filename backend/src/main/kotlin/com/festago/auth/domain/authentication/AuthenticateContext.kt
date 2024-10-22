package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class AuthenticateContext(
    var authentication: Authentication = AnonymousAuthentication,
) {

    val id: Long?
        get() = authentication.id

    val role: Role
        get() = authentication.role
}
