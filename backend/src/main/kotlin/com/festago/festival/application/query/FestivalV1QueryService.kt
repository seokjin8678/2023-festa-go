package com.festago.festival.application.query

import com.festago.common.exception.ValidException
import com.festago.festival.dto.FestivalV1QueryRequest
import com.festago.festival.dto.FestivalV1Response
import com.festago.festival.infrastructure.repository.query.FestivalSearchCondition
import com.festago.festival.infrastructure.repository.query.FestivalV1QueryDslRepository
import java.time.Clock
import java.time.LocalDate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FestivalV1QueryService(
    private val festivalV1QueryDslRepository: FestivalV1QueryDslRepository,
    private val clock: Clock,
) {

    fun findFestivals(pageable: Pageable, request: FestivalV1QueryRequest): Slice<FestivalV1Response> {
        validateCursor(request.lastFestivalId, request.lastStartDate)
        return festivalV1QueryDslRepository.findBy(
            FestivalSearchCondition(
                request.filter,
                request.location,
                request.lastStartDate,
                request.lastFestivalId,
                pageable,
                LocalDate.now(clock)
            )
        )
    }

    private fun validateCursor(lastFestivalId: Long?, lastStartDate: LocalDate?) {
        if (lastFestivalId == null && lastStartDate == null) {
            return
        }
        if (lastFestivalId != null && lastStartDate != null) {
            return
        }
        throw ValidException("festivalId, lastStartDate 두 값 모두 요청하거나 요청하지 않아야합니다.")
    }
}

