package com.festago.admin.infrastructure.repository.query

import com.festago.admin.dto.festival.AdminFestivalDetailV1Response
import com.festago.admin.dto.festival.QAdminFestivalDetailV1Response
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.school.domain.QSchool.school
import org.springframework.stereotype.Repository

@Repository
class AdminFestivalDetailV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findDetail(festivalId: Long): AdminFestivalDetailV1Response? {
        return queryDslHelper.fetchOne {
            it.select(
                QAdminFestivalDetailV1Response(
                    festival.id,
                    festival.name,
                    school.id,
                    school.name,
                    festival.festivalDuration.startDate,
                    festival.festivalDuration.endDate,
                    festival.posterImageUrl,
                    festival.createdAt,
                    festival.updatedAt
                )
            )
                .from(festival)
                .innerJoin(school).on(school.id.eq(festival.school.id))
                .where(festival.id.eq(festivalId))
        }
    }
}
