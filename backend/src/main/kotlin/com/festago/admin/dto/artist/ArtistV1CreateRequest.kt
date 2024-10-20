package com.festago.admin.dto.artist

import com.festago.artist.dto.command.ArtistCreateCommand
import jakarta.validation.constraints.NotBlank

data class ArtistV1CreateRequest(
    @field:NotBlank
    val name: String,
    val profileImageUrl: String?,
    val backgroundImageUrl: String?,
) {

    fun toCommand(): ArtistCreateCommand {
        return ArtistCreateCommand(name, profileImageUrl, backgroundImageUrl)
    }
}
