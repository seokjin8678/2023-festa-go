package com.festago.admin.dto.socialmedia

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaType
import com.querydsl.core.annotations.QueryProjection

data class AdminSocialMediaV1Response @QueryProjection constructor(
    val id: Long,
    val ownerId: Long,
    val ownerType: OwnerType,
    val socialMediaType: SocialMediaType,
    val name: String,
    val logoUrl: String,
    val url: String,
)
