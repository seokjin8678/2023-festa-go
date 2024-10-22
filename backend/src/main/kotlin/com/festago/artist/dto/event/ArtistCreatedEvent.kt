package com.festago.artist.dto.event

import com.festago.artist.domain.Artist

data class ArtistCreatedEvent(
    val artist: Artist,
)
