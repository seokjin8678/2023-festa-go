package com.festago.bookmark.dto.v1

import com.festago.festival.dto.FestivalV1Response
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FestivalBookmarkV1Response @QueryProjection constructor(
    val festival: FestivalV1Response,
    val createdAt: LocalDateTime,
)
