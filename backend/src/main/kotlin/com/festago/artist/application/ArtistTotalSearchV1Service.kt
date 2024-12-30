package com.festago.artist.application

import com.festago.artist.dto.ArtistSearchStageCountV1Response
import com.festago.artist.dto.ArtistTotalSearchV1Response
import java.time.Clock
import java.time.LocalDate
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArtistTotalSearchV1Service(
    private val artistSearchV1QueryService: ArtistSearchV1QueryService,
    private val artistSearchStageCountV1QueryService: ArtistSearchStageCountV1QueryService,
    private val clock: Clock,
) {

    @Cacheable(cacheNames = [CACHE_NAME], key = "#keyword")
    fun findAllByKeyword(keyword: String): List<ArtistTotalSearchV1Response> {
        val artists = artistSearchV1QueryService.findAllByKeyword(keyword)
        val artistIdToStageCount = artistSearchStageCountV1QueryService.findArtistsStageCountAfterDateTime(
            artistIds = artists.map { it.id },
            now = LocalDate.now(clock).atStartOfDay()
        )
        return artists.map {
            ArtistTotalSearchV1Response.of(
                artistResponse = it,
                stageCount = artistIdToStageCount[it.id] ?: ArtistSearchStageCountV1Response.EMPTY
            )
        }
    }

    companion object {
        const val CACHE_NAME = "ARTIST_TOTAL_SEARCH_V1"
    }
}
