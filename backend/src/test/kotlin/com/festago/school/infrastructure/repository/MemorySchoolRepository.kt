package com.festago.school.infrastructure.repository

import com.festago.school.domain.School
import com.festago.school.domain.SchoolRepository
import com.festago.support.AbstractMemoryRepository

class MemorySchoolRepository : AbstractMemoryRepository<School>(), SchoolRepository {

    override fun existsByDomain(domain: String): Boolean {
        return memory.values.any { it.domain == domain }
    }

    override fun existsByName(name: String): Boolean {
        return memory.values.any { it.name == name }
    }

    override fun findByName(name: String): School? {
        return memory.values.firstOrNull { it.name == name }
    }
}
