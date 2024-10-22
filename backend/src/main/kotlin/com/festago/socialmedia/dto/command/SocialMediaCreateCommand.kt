package com.festago.socialmedia.dto.command

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaType

data class SocialMediaCreateCommand(
    val ownerId: Long,
    val ownerType: OwnerType,
    val socialMediaType: SocialMediaType,
    val name: String,
    val logoUrl: String?,
    val url: String,
)
