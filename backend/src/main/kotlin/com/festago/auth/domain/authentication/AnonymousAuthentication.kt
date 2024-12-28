package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role

data object AnonymousAuthentication : Authentication {

    override val memberId = null

    override val role = Role.ANONYMOUS
}
