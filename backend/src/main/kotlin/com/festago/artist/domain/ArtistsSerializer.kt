package com.festago.artist.domain

fun interface ArtistsSerializer {
    fun serialize(artists: List<Artist>): String
}
