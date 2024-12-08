package com.festago.artist.dto.event

import com.festago.artist.domain.Artist

data class ArtistUpdatedEvent(
    val artist: Artist,
    val oldArtist: Artist,
)
