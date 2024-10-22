package com.festago.artist.infrastructure

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistQueryModel
import com.festago.artist.domain.ArtistsSerializer
import com.festago.common.exception.UnexpectedException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class JsonArtistsSerializer(
    private val objectMapper: ObjectMapper,
) : ArtistsSerializer {

    override fun serialize(artists: List<Artist>): String {
        val artistQueryModels = artists.map { ArtistQueryModel.from(it) }
        try {
            return objectMapper.writeValueAsString(artistQueryModels)
        } catch (e: JsonProcessingException) {
            log.error(e) { e.message }
            throw UnexpectedException("Artist 목록을 직렬화 하는 중에 문제가 발생했습니다.")
        }
    }
}
