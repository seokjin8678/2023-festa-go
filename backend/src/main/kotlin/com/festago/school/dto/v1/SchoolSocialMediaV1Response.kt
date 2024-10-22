package com.festago.school.dto.v1

import com.festago.socialmedia.domain.SocialMediaType
import com.querydsl.core.annotations.QueryProjection

data class SchoolSocialMediaV1Response @QueryProjection constructor(
    val type: SocialMediaType,
    val name: String,
    val logoUrl: String,
    val url: String,
)
