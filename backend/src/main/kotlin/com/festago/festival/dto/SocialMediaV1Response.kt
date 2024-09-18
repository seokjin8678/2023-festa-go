package com.festago.festival.dto

import com.festago.socialmedia.domain.SocialMediaType
import com.querydsl.core.annotations.QueryProjection

data class SocialMediaV1Response @QueryProjection constructor(
    val type: SocialMediaType,
    val name: String,
    val logoUrl: String,
    val url: String
)
