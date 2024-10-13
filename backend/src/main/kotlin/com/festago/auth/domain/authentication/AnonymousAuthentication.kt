package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role

object AnonymousAuthentication : Authentication {

    override val id = null

    override val role = Role.ANONYMOUS
}
