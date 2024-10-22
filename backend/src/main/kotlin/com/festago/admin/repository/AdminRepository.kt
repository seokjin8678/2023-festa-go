package com.festago.admin.repository

import com.festago.admin.domain.Admin
import org.springframework.data.repository.Repository

interface AdminRepository : Repository<Admin, Long> {

    fun save(admin: Admin): Admin

    fun findById(adminId: Long): Admin?

    fun findByUsername(username: String): Admin?

    fun existsByUsername(username: String): Boolean
}
