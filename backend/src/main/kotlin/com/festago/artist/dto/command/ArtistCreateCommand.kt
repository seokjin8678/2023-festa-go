package com.festago.artist.dto.command

data class ArtistCreateCommand(
    val name: String,
    val profileImageUrl: String? = null,
    val backgroundImageUrl: String? = null,
)
