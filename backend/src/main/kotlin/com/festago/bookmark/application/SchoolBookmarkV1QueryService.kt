package com.festago.bookmark.application

import com.festago.bookmark.dto.v1.SchoolBookmarkV1Response
import com.festago.bookmark.repository.v1.SchoolBookmarkV1QuerydslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SchoolBookmarkV1QueryService(
    private val schoolBookmarkV1QuerydslRepository: SchoolBookmarkV1QuerydslRepository,
) {

    fun findAllByMemberId(memberId: Long): List<SchoolBookmarkV1Response> {
        return schoolBookmarkV1QuerydslRepository.findAllByMemberId(memberId)
    }
}

