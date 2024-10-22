package com.festago.bookmark.dto.v1

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ArtistBookmarkV1Response @QueryProjection constructor(
    val artist: ArtistBookmarkInfoV1Response,
    val createdAt: LocalDateTime,
)
