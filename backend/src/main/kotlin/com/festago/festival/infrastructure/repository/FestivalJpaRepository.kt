package com.festago.festival.infrastructure.repository

import com.festago.festival.domain.Festival
import org.springframework.data.jpa.repository.JpaRepository

internal interface FestivalJpaRepository : JpaRepository<Festival, Long> {

    fun existsBySchoolId(schoolId: Long): Boolean
}
