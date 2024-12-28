package com.festago.school.domain

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException

fun SchoolRepository.getOrThrow(id: Long): School {
    return findById(id) ?: throw NotFoundException(ErrorCode.SCHOOL_NOT_FOUND)
}

interface SchoolRepository {
    fun save(school: School): School

    fun findById(id: Long): School?

    fun deleteById(id: Long)

    fun existsById(id: Long): Boolean

    fun existsByDomain(domain: String): Boolean

    fun existsByName(name: String): Boolean

    fun findByName(name: String): School?
}
