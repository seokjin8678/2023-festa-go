package com.festago.auth.domain.authentication

import com.festago.auth.domain.Role

/**
 * 인증 정보를 담은 인터페이스
 *
 * 구현체는 반드시 getRole()에 null을 반환하지 않도록 해야한다.
 */
sealed interface Authentication {
    val id: Long?

    val role: Role
}
