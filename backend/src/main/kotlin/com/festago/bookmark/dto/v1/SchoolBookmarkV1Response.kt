package com.festago.bookmark.dto.v1

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class SchoolBookmarkV1Response @QueryProjection constructor(
    val school: SchoolBookmarkInfoV1Response,
    val createdAt: LocalDateTime,
)
