package com.festago.admin.repository

import com.festago.admin.domain.Admin
import com.festago.support.AbstractMemoryRepository

class MemoryAdminRepository : AbstractMemoryRepository<Admin>(), AdminRepository {
    override fun findByUsername(username: String): Admin? {
        return memory.values.firstOrNull { it.username == username }
    }

    override fun existsByUsername(username: String): Boolean {
        return memory.values.any { it.username == username }
    }
}
