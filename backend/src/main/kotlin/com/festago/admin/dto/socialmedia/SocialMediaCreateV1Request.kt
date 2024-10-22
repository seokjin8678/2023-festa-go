package com.festago.admin.dto.socialmedia

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.socialmedia.dto.command.SocialMediaCreateCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialMediaCreateV1Request(
    @field:NotNull
    val ownerId: Long,
    @field:NotNull
    val ownerType: OwnerType,
    @field:NotNull
    val socialMediaType: SocialMediaType,
    @field:NotBlank
    val name: String,
    val logoUrl: String?,
    @field:NotBlank
    val url: String,
) {
    fun toCommand(): SocialMediaCreateCommand {
        return SocialMediaCreateCommand(
            ownerId = ownerId,
            name = name,
            socialMediaType = socialMediaType,
            ownerType = ownerType,
            url = url,
            logoUrl = logoUrl,
        )
    }
}
