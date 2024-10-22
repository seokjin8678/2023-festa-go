package com.festago.school.repository

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.domain.School
import org.springframework.data.repository.Repository

fun SchoolRepository.getOrThrow(id: Long): School {
    return findById(id) ?: throw NotFoundException(ErrorCode.SCHOOL_NOT_FOUND)
}

interface SchoolRepository : Repository<School, Long> {
    fun save(school: School): School

    fun findById(id: Long): School?

    fun deleteById(id: Long)

    fun existsById(id: Long): Boolean

    fun existsByDomain(domain: String): Boolean

    fun existsByName(name: String): Boolean

    fun findByName(name: String): School?
}
