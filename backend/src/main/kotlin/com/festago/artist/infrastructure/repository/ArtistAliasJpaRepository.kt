package com.festago.artist.infrastructure.repository

import com.festago.artist.domain.ArtistAlias
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface ArtistAliasJpaRepository : JpaRepository<ArtistAlias, Long> {

    fun findByArtistIdAndAlias(artistId: Long, alias: String): ArtistAlias?

    @Modifying
    @Query("DELETE FROM ArtistAlias aa WHERE aa.artistId = :artistId")
    fun deleteByArtistId(@Param("artistId") artistId: Long)
}
