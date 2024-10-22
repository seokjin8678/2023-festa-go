package com.festago.admin.dto.festival

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalDateTime

data class AdminFestivalDetailV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val schoolId: Long,
    val schoolName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val posterImageUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
