package com.festago.auth.domain

import java.util.UUID

interface RefreshTokenRepository {

    fun save(refreshToken: RefreshToken): RefreshToken

    fun findById(id: UUID): RefreshToken?

    fun deleteById(id: UUID)
}
