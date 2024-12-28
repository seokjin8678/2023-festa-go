package com.festago.festival.infrastructure.repository.query

import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.school.dto.v1.QSchoolUpcomingFestivalStartDateV1Response
import com.festago.school.dto.v1.SchoolUpcomingFestivalStartDateV1Response
import java.time.LocalDate
import org.springframework.stereotype.Repository

@Repository
class RecentSchoolFestivalV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {
    fun findRecentSchoolFestivals(
        schoolIds: List<Long>,
        now: LocalDate,
    ): List<SchoolUpcomingFestivalStartDateV1Response> {
        return queryDslHelper.select(
            QSchoolUpcomingFestivalStartDateV1Response(
                festival.school.id,
                festival.festivalDuration.startDate.min()
            )
        )
            .from(festival)
            .where(
                festival.school.id.`in`(schoolIds).and(festival.festivalDuration.endDate.goe(now))
            )
            .groupBy(festival.school.id)
            .fetch()
    }
}
