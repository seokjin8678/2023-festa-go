package com.festago.school.dto.v1

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class SchoolUpcomingFestivalStartDateV1Response @QueryProjection constructor(
    val schoolId: Long,
    val startDate: LocalDate,
)
