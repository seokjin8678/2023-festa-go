package com.festago.school.infrastructure.repository

import com.festago.school.domain.School
import org.springframework.data.jpa.repository.JpaRepository

internal interface SchoolJpaRepository : JpaRepository<School, Long> {

    fun existsByDomain(domain: String): Boolean

    fun existsByName(name: String): Boolean

    fun findByName(name: String): School?
}
