package com.festago.artist.infrastructure

import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistsSerializer

class DelimiterArtistsSerializer(
    private val delimiter: String,
) : ArtistsSerializer {

    override fun serialize(artists: List<Artist>): String {
        return artists.joinToString(separator = delimiter) { it.name }
    }
}
