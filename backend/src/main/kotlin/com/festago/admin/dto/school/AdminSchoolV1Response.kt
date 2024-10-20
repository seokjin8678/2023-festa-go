package com.festago.admin.dto.school

import com.festago.school.domain.SchoolRegion
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class AdminSchoolV1Response @QueryProjection constructor(
    val id: Long,
    val domain: String,
    val name: String,
    val region: SchoolRegion,
    val logoUrl: String,
    val backgroundImageUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
