package com.festago.artist.repository

import com.festago.artist.domain.Artist
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import org.springframework.data.repository.Repository

fun ArtistRepository.getOrThrow(artistId: Long): Artist {
    return findById(artistId) ?: throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
}

interface ArtistRepository : Repository<Artist, Long> {

    fun save(artist: Artist): Artist

    fun deleteById(artistId: Long)

    fun findById(artistId: Long): Artist?

    fun countByIdIn(artistIds: List<Long>): Long

    fun findByIdIn(artistIds: Collection<Long>): List<Artist>

    fun existsById(artistId: Long): Boolean

    fun existsByName(name: String): Boolean
}
