package com.festago.festival.infrastructure.repository.query

import com.festago.school.domain.SchoolRegion
import java.time.LocalDate
import org.springframework.data.domain.Pageable

data class FestivalSearchCondition(
    val filter: FestivalFilter,
    val region: SchoolRegion,
    val lastStartDate: LocalDate?,
    val lastFestivalId: Long?,
    val pageable: Pageable,
    val currentTime: LocalDate,
)
