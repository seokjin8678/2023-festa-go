package com.festago.festival.dto

import com.festago.festival.infrastructure.repository.query.FestivalFilter
import com.festago.school.domain.SchoolRegion
import java.time.LocalDate

data class FestivalV1QueryRequest(
    val location: SchoolRegion,
    val filter: FestivalFilter,
    val lastFestivalId: Long? = null,
    val lastStartDate: LocalDate? = null,
)
