package com.festago.school.infrastructure.repository

import com.festago.school.domain.School
import com.festago.school.domain.SchoolRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class SchoolRepositoryImpl(
    private val schoolJpaRepository: SchoolJpaRepository,
) : SchoolRepository {

    override fun save(school: School): School {
        return schoolJpaRepository.save(school)
    }

    override fun findById(id: Long): School? {
        return schoolJpaRepository.findByIdOrNull(id)
    }

    override fun deleteById(id: Long) {
        schoolJpaRepository.deleteById(id)
    }

    override fun existsById(id: Long): Boolean {
        return schoolJpaRepository.existsById(id)
    }

    override fun existsByDomain(domain: String): Boolean {
        return schoolJpaRepository.existsByDomain(domain)
    }

    override fun existsByName(name: String): Boolean {
        return schoolJpaRepository.existsByName(name)
    }

    override fun findByName(name: String): School? {
        return schoolJpaRepository.findByName(name)
    }
}
