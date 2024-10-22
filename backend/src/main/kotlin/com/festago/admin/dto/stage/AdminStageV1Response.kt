package com.festago.admin.dto.stage

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class AdminStageV1Response @QueryProjection constructor(
    val id: Long,
    val startDateTime: LocalDateTime,
    val ticketOpenTime: LocalDateTime,
    val artists: List<AdminStageArtistV1Response>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
