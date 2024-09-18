package com.festago.support

import com.festago.auth.domain.Role
import io.kotest.core.extensions.Extension

class WithMockAuthExtension(
    val id: Long = 1,
    val role: Role = Role.MEMBER,
): Extension