package com.festago.auth.infrastructure.repository

import com.festago.auth.domain.RefreshToken
import com.festago.auth.domain.RefreshTokenRepository
import java.util.UUID

class MemoryRefreshTokenRepository : RefreshTokenRepository {

    private val memory = HashMap<UUID, RefreshToken>()

    override fun save(refreshToken: RefreshToken): RefreshToken {
        memory[refreshToken.id] = refreshToken
        return refreshToken
    }

    override fun findById(id: UUID): RefreshToken? {
        return memory[id]
    }

    override fun deleteById(id: UUID) {
        memory.remove(id)
    }
}
