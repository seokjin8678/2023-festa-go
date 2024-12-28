package com.festago.admin.infrastructure.repository.query

import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import java.time.LocalDate
import org.springframework.stereotype.Repository

@Repository
class AdminFestivalIdResolverQueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findFestivalIdsByStartDatePeriod(to: LocalDate, end: LocalDate): List<Long> {
        return queryDslHelper.select(festival.id)
            .from(festival)
            .where(
                festival.festivalDuration.startDate.goe(to)
                    .and(festival.festivalDuration.startDate.loe(end))
            )
            .fetch()
    }
}
