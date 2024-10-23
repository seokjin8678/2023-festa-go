package com.festago.artist.application.alias

import com.festago.artist.domain.ArtistAlias
import com.festago.artist.repository.alias.ArtistAliasRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArtistAliasService(
    private val artistAliasRepository: ArtistAliasRepository,
) {

    fun createArtistAlias(artistId: Long, alias: String) {
        artistAliasRepository.save(
            ArtistAlias(
                artistId = artistId,
                alias = alias
            )
        )
    }

    fun updateArtistAlias(artistId: Long, alias: String) {
        val artistAlias = artistAliasRepository.findByAlias(alias) ?: return
        artistAlias.updateAlias(alias)
    }

    fun deleteArtistAlias(artistId: Long) {
        artistAliasRepository.deleteByArtistId(artistId)
    }
}
