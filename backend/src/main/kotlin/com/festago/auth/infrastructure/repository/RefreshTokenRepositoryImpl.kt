package com.festago.auth.infrastructure.repository

import com.festago.auth.domain.RefreshToken
import com.festago.auth.domain.RefreshTokenRepository
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class RefreshTokenRepositoryImpl(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken): RefreshToken {
        return refreshTokenJpaRepository.save(refreshToken)
    }

    override fun findById(id: UUID): RefreshToken? {
        return refreshTokenJpaRepository.findByIdOrNull(id)
    }

    override fun deleteById(id: UUID) {
        return refreshTokenJpaRepository.deleteById(id)
    }
}
