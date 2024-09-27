package com.festago.bookmark.application

import com.festago.bookmark.dto.v1.ArtistBookmarkV1Response
import com.festago.bookmark.repository.v1.ArtistBookmarkV1QueryDslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArtistBookmarkV1QueryService(
    private val artistBookmarkV1QueryDslRepository: ArtistBookmarkV1QueryDslRepository,
) {

    fun findArtistBookmarksByMemberId(memberId: Long): List<ArtistBookmarkV1Response> {
        return artistBookmarkV1QueryDslRepository.findByMemberId(memberId)
    }
}
