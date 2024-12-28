package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role

class MemberAuthentication(
    override val memberId: Long,
) : Authentication {

    override val role = Role.MEMBER
}
