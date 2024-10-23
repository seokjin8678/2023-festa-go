package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role

data object AnonymousAuthentication : Authentication {

    override val id = null

    override val role = Role.ANONYMOUS
}
