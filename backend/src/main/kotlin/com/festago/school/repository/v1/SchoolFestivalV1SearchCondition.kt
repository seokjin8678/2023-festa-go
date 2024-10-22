package com.festago.school.repository.v1

import java.time.LocalDate
import org.springframework.data.domain.Pageable

data class SchoolFestivalV1SearchCondition(
    val lastFestivalId: Long? = null,
    val lastStartDate: LocalDate? = null,
    val isPast: Boolean,
    val pageable: Pageable,
)
