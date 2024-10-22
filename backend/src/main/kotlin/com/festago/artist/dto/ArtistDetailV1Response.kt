package com.festago.artist.dto

import com.querydsl.core.annotations.QueryProjection

data class ArtistDetailV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
    val backgroundImageUrl: String,
    val socialMedias: List<ArtistMediaV1Response>,
)
