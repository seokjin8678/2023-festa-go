package com.festago.artist.infrastructure.repository

import com.festago.artist.domain.ArtistAlias
import com.festago.artist.domain.ArtistAliasRepository
import org.springframework.stereotype.Repository

@Repository
internal class ArtistAliasRepositoryImpl(
    private val artistAliasJpaRepository: ArtistAliasJpaRepository,
) : ArtistAliasRepository {

    override fun save(artist: ArtistAlias): ArtistAlias {
        return artistAliasJpaRepository.save(artist)
    }

    override fun findByArtistIdAndAlias(artistId: Long, alias: String): ArtistAlias? {
        return artistAliasJpaRepository.findByArtistIdAndAlias(artistId, alias)
    }

    override fun deleteByArtistId(artistId: Long) {
        artistAliasJpaRepository.deleteByArtistId(artistId)
    }
}
