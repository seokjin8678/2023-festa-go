package com.festago.bookmark.application

import com.festago.bookmark.dto.v1.FestivalBookmarkV1Response
import com.festago.bookmark.infrastructure.repository.query.FestivalBookmarkOrder
import com.festago.bookmark.infrastructure.repository.query.v1.FestivalBookmarkV1QueryDslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FestivalBookmarkV1QueryService(
    private val festivalBookmarkV1QueryDslRepository: FestivalBookmarkV1QueryDslRepository,
) {

    fun findBookmarkedFestivalIds(memberId: Long): List<Long> {
        return festivalBookmarkV1QueryDslRepository.findBookmarkedFestivalIds(memberId)
    }

    fun findBookmarkedFestivals(
        memberId: Long,
        festivalIds: List<Long>,
        festivalBookmarkOrder: FestivalBookmarkOrder,
    ): List<FestivalBookmarkV1Response> {
        return festivalBookmarkV1QueryDslRepository.findBookmarkedFestivals(
            memberId,
            festivalIds,
            festivalBookmarkOrder
        )
    }
}
