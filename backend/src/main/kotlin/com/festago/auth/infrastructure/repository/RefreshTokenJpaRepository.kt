package com.festago.auth.infrastructure.repository

import com.festago.auth.domain.RefreshToken
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

internal interface RefreshTokenJpaRepository : JpaRepository<RefreshToken, UUID>
