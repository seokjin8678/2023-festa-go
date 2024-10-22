package com.festago.admin.dto.festival

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class AdminFestivalV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val schoolName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val stageCount: Long,
)
