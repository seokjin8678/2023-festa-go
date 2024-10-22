package com.festago.admin.dto.artist

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class AdminArtistV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
    val backgroundImageUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
