package com.festago.artist.dto.command

data class ArtistCreateCommand(
    val name: String,
    val profileImageUrl: String?,
    val backgroundImageUrl: String?,
)
