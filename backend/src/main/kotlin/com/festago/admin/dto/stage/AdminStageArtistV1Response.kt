package com.festago.admin.dto.stage

import com.querydsl.core.annotations.QueryProjection

data class AdminStageArtistV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
)
