package com.festago.admin.domain

interface AdminRepository {

    fun save(admin: Admin): Admin

    fun findById(adminId: Long): Admin?

    fun findByUsername(username: String): Admin?

    fun existsByUsername(username: String): Boolean
}
