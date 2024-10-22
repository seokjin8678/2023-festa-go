package com.festago.artist.dto.command

data class ArtistUpdateCommand(
    val name: String,
    val profileImageUrl: String?,
    val backgroundImageUrl: String?,
)
