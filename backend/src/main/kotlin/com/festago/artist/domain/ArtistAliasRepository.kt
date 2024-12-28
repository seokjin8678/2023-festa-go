package com.festago.artist.domain

interface ArtistAliasRepository {

    fun save(artist: ArtistAlias): ArtistAlias

    fun findByArtistIdAndAlias(artistId: Long, alias: String): ArtistAlias?

    fun deleteByArtistId(artistId: Long)
}
