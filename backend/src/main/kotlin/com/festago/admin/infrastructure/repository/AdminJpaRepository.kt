package com.festago.admin.infrastructure.repository

import com.festago.admin.domain.Admin
import org.springframework.data.jpa.repository.JpaRepository

internal interface AdminJpaRepository : JpaRepository<Admin, Long> {

    fun findByUsername(username: String): Admin?

    fun existsByUsername(username: String): Boolean
}
