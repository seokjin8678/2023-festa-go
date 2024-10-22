package com.festago.school.dto.v1

import com.querydsl.core.annotations.QueryProjection

data class SchoolSearchV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val logoUrl: String,
)
