package com.festago.festival.repository

import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.festival.dto.FestivalV1Response
import com.festago.festival.dto.QFestivalV1Response
import com.festago.festival.dto.QSchoolV1Response
import com.festago.school.domain.QSchool.school
import org.springframework.stereotype.Repository

@Repository
class PopularFestivalV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {
    /**
     * 아직 명확한 추천 축제 기준이 없으므로 생성 시간(식별자) 내림차순으로 반환하도록 함
     */
    fun findPopularFestivals(): List<FestivalV1Response> {
        return queryDslHelper.select(
            QFestivalV1Response(
                festival.id,
                festival.name,
                festival.festivalDuration.startDate,
                festival.festivalDuration.endDate,
                festival.posterImageUrl,
                QSchoolV1Response(
                    school.id,
                    school.name
                ),
                festivalQueryInfo.artistInfo
            )
        )
            .from(festival)
            .innerJoin(school).on(school.id.eq(festival.school.id))
            .innerJoin(festivalQueryInfo).on(festivalQueryInfo.festivalId.eq(festival.id))
            .where(festivalQueryInfo.artistInfo.ne("[]"))
            .orderBy(festival.id.desc())
            .limit(POPULAR_FESTIVAL_LIMIT_COUNT)
            .fetch()
    }

    companion object {
        private const val POPULAR_FESTIVAL_LIMIT_COUNT = 7L
    }
}
