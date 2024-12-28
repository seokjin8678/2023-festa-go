package com.festago.admin.infrastructure.repository

import com.festago.admin.domain.Admin
import com.festago.admin.domain.AdminRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class AdminRepositoryImpl(
    private val adminJpaRepository: AdminJpaRepository,
) : AdminRepository {

    override fun save(admin: Admin): Admin {
        return adminJpaRepository.save(admin)
    }

    override fun findById(adminId: Long): Admin? {
        return adminJpaRepository.findByIdOrNull(adminId)
    }

    override fun findByUsername(username: String): Admin? {
        return adminJpaRepository.findByUsername(username)
    }

    override fun existsByUsername(username: String): Boolean {
        return adminJpaRepository.existsByUsername(username)
    }
}
