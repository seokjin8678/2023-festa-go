package com.festago.festival.repository

import com.festago.artist.domain.QArtist.artist
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.festival.dto.FestivalSearchV1Response
import com.festago.festival.dto.QFestivalSearchV1Response
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.QStageArtist.stageArtist
import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.stereotype.Repository

@Repository
class FestivalArtistNameSearchV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
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
            .from(artist)
            .innerJoin(stageArtist).on(stageArtist.artistId.eq(artist.id))
            .innerJoin(stage).on(stage.id.eq(stageArtist.stageId))
            .innerJoin(festival).on(festival.id.eq(stage.festival.id))
            .innerJoin(festivalQueryInfo).on(festival.id.eq(festivalQueryInfo.festivalId))
            .where(eqOrContains(keyword))
            .fetch()
    }

    private fun eqOrContains(keyword: String): BooleanExpression {
        return if (keyword.length == 1) artist.name.eq(keyword) else artist.name.contains(keyword)
    }
}
