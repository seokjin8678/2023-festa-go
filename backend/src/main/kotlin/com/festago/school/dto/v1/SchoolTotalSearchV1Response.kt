package com.festago.school.dto.v1

import java.time.LocalDate

data class SchoolTotalSearchV1Response(
    val id: Long,
    val name: String,
    val logoUrl: String,
    val upcomingFestivalStartDate: LocalDate?,
)
