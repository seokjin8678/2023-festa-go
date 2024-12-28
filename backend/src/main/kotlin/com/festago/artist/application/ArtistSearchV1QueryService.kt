package com.festago.artist.application

import com.festago.artist.dto.ArtistSearchV1Response
import com.festago.artist.infrastructure.repository.query.ArtistSearchV1QueryDslRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArtistSearchV1QueryService(
    private val artistSearchV1QueryDslRepository: ArtistSearchV1QueryDslRepository,
) {

    fun findAllByKeyword(keyword: String): List<ArtistSearchV1Response> {
        val response = getResponse(keyword)
        if (response.size >= MAX_SEARCH_COUNT) {
            throw BadRequestException(ErrorCode.BROAD_SEARCH_KEYWORD)
        }
        return response
    }

    private fun getResponse(keyword: String): List<ArtistSearchV1Response> {
        if (keyword.length == 1) {
            return artistSearchV1QueryDslRepository.findAllByEqual(keyword)
        }
        return artistSearchV1QueryDslRepository.findAllByLike(keyword)
    }

    companion object {
        private const val MAX_SEARCH_COUNT = 10
    }
}
