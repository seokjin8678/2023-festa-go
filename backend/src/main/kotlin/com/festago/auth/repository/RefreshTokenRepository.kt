package com.festago.auth.repository

import com.festago.auth.domain.RefreshToken
import java.util.UUID
import org.springframework.data.repository.Repository

interface RefreshTokenRepository : Repository<RefreshToken, UUID> {

    fun save(refreshToken: RefreshToken): RefreshToken

    fun findById(id: UUID): RefreshToken?

    fun deleteById(id: UUID)
}
