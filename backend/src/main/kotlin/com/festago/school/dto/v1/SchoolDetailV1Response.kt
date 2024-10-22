package com.festago.school.dto.v1

import com.querydsl.core.annotations.QueryProjection

data class SchoolDetailV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val logoUrl: String,
    val backgroundImageUrl: String,
    val socialMedias: List<SchoolSocialMediaV1Response>,
)
