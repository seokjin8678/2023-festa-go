package com.festago.artist.dto

import com.querydsl.core.annotations.QueryProjection

data class ArtistSearchV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
)
