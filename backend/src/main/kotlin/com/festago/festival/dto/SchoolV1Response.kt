package com.festago.festival.dto

import com.querydsl.core.annotations.QueryProjection

data class SchoolV1Response @QueryProjection constructor(
    val id: Long,
    val name: String
)
