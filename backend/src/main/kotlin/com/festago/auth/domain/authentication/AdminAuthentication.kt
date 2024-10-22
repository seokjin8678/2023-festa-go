package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role

class AdminAuthentication(
    override val id: Long,
) : Authentication {

    override val role = Role.ADMIN
}
