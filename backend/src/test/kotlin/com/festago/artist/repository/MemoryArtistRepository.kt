package com.festago.artist.repository

import com.festago.artist.domain.Artist
import com.festago.support.AbstractMemoryRepository

class MemoryArtistRepository : AbstractMemoryRepository<Artist>(), ArtistRepository {

    override fun countByIdIn(artistIds: List<Long>): Long {
        return memory.values.count { it.id in artistIds }.toLong()
    }

    override fun findByIdIn(artistIds: Collection<Long>): List<Artist> {
        return memory.values.filter { it.id in artistIds }
    }

    override fun existsByName(name: String): Boolean {
        return memory.values.any { it.name == name }
    }
}
