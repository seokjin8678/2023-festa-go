package com.festago.school.repository

import com.festago.school.domain.School
import com.festago.support.AbstractMemoryRepositoryKt

class MemorySchoolRepository : AbstractMemoryRepositoryKt<School>(), SchoolRepository {

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
