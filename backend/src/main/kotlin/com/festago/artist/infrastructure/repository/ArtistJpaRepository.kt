package com.festago.artist.infrastructure.repository

import com.festago.artist.domain.Artist
import org.springframework.data.jpa.repository.JpaRepository

internal interface ArtistJpaRepository : JpaRepository<Artist, Long> {

    fun countByIdIn(artistIds: List<Long>): Long

    fun findByIdIn(artistIds: Collection<Long>): List<Artist>

    fun existsByName(name: String): Boolean
}
