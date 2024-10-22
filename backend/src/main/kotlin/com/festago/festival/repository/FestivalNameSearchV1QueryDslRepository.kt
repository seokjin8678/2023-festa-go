package com.festago.festival.repository

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.festival.dto.FestivalSearchV1Response
import com.festago.festival.dto.QFestivalSearchV1Response
import org.springframework.stereotype.Repository

@Repository
class FestivalNameSearchV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper
) {
    fun executeSearch(keyword: String): List<FestivalSearchV1Response> {
        if (keyword.isEmpty()) {
            throw BadRequestException(ErrorCode.INVALID_KEYWORD)
        }

        return queryDslHelper.select(
            QFestivalSearchV1Response(
                festival.id,
                festival.name,
                festival.festivalDuration.startDate,
                festival.festivalDuration.endDate,
                festival.posterImageUrl,
                festivalQueryInfo.artistInfo
            )
        )
            .from(festival)
            .innerJoin(festivalQueryInfo).on(festival.id.eq(festivalQueryInfo.festivalId))
            .where(festival.name.contains(keyword))
            .fetch()
    }
}
