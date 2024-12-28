package com.festago.festival.application.query

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.dto.FestivalDetailV1Response
import com.festago.festival.infrastructure.repository.query.FestivalDetailV1QueryDslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FestivalDetailV1QueryService(
    private val festivalDetailV1QueryDslRepository: FestivalDetailV1QueryDslRepository,
) {

    fun findFestivalDetail(festivalId: Long): FestivalDetailV1Response {
        return festivalDetailV1QueryDslRepository.findFestivalDetail(festivalId)
            ?: throw NotFoundException(ErrorCode.FESTIVAL_NOT_FOUND)
    }
}
