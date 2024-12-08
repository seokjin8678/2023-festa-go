package com.festago.artist.repository.alias

import com.festago.artist.domain.ArtistAlias
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface ArtistAliasRepository : Repository<ArtistAlias, Long> {

    fun save(artist: ArtistAlias): ArtistAlias

    fun findByArtistIdAndAlias(artistId: Long, alias: String): ArtistAlias?

    @Modifying
    @Query("DELETE FROM ArtistAlias aa WHERE aa.artistId = :artistId")
    fun deleteByArtistId(@Param("artistId") artistId: Long)
}
