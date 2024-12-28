package com.festago.artist.infrastructure.repository

import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class ArtistRepositoryImpl(
    private val artistJpaRepository: ArtistJpaRepository,
) : ArtistRepository {

    override fun save(artist: Artist): Artist {
        return artistJpaRepository.save(artist)
    }

    override fun deleteById(artistId: Long) {
        artistJpaRepository.deleteById(artistId)
    }

    override fun findById(artistId: Long): Artist? {
        return artistJpaRepository.findByIdOrNull(artistId)
    }

    override fun countByIdIn(artistIds: List<Long>): Long {
        return artistJpaRepository.countByIdIn(artistIds)
    }

    override fun findByIdIn(artistIds: Collection<Long>): List<Artist> {
        return artistJpaRepository.findByIdIn(artistIds)
    }

    override fun existsById(artistId: Long): Boolean {
        return artistJpaRepository.existsById(artistId)
    }

    override fun existsByName(name: String): Boolean {
        return artistJpaRepository.existsByName(name)
    }
}
