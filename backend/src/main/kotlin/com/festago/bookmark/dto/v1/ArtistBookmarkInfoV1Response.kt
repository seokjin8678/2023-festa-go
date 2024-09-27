package com.festago.bookmark.dto.v1

import com.querydsl.core.annotations.QueryProjection

data class ArtistBookmarkInfoV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
)
