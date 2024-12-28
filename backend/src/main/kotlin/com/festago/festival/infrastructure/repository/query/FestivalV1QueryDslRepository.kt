package com.festago.festival.infrastructure.repository.query

import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.festival.dto.FestivalV1Response
import com.festago.festival.dto.QFestivalV1Response
import com.festago.festival.dto.QSchoolV1Response
import com.festago.school.domain.QSchool.school
import com.festago.school.domain.SchoolRegion
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import java.time.LocalDate
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
class FestivalV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {
    fun findBy(searchCondition: FestivalSearchCondition): Slice<FestivalV1Response> {
        val (filter, region, lastStartDate, lastFestivalId, pageable, currentTime) = searchCondition
        return queryDslHelper.applySlice(pageable) { queryFactory ->
            queryFactory.select(
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
                .where(
                    applyPaging(
                        filter,
                        currentTime,
                        lastFestivalId,
                        lastStartDate
                    ),
                    applyEqRegion(region)
                )
                .orderBy(*dynamicOrderBy(filter))
        }
    }

    private fun applyPaging(
        filter: FestivalFilter,
        currentTime: LocalDate,
        lastFestivalId: Long?,
        lastStartDate: LocalDate?,
    ): BooleanExpression {
        if (lastFestivalId != null && lastStartDate != null) {
            return getCursorBasedBooleanExpression(filter, currentTime, lastFestivalId, lastStartDate)
        }
        return getDefaultBooleanExpression(filter, currentTime)
    }

    private fun getCursorBasedBooleanExpression(
        filter: FestivalFilter,
        currentTime: LocalDate,
        lastFestivalId: Long,
        lastStartDate: LocalDate,
    ): BooleanExpression {
        return when (filter) {
            FestivalFilter.PLANNED -> festival.festivalDuration.startDate.gt(lastStartDate)
                .or(
                    festival.festivalDuration.startDate.eq(lastStartDate)
                        .and(festival.id.gt(lastFestivalId))
                )

            FestivalFilter.PROGRESS -> festival.festivalDuration.startDate.lt(lastStartDate)
                .or(
                    festival.festivalDuration.startDate.eq(lastStartDate)
                        .and(festival.id.gt(lastFestivalId))
                )
                .and(festival.festivalDuration.endDate.goe(currentTime))

            FestivalFilter.END -> festival.festivalDuration.endDate.lt(currentTime)
        }
    }

    private fun getDefaultBooleanExpression(
        filter: FestivalFilter,
        currentTime: LocalDate,
    ): BooleanExpression {
        return when (filter) {
            FestivalFilter.PLANNED -> festival.festivalDuration.startDate.gt(currentTime)
            FestivalFilter.PROGRESS -> festival.festivalDuration.startDate.loe(currentTime)
                .and(festival.festivalDuration.endDate.goe(currentTime))

            FestivalFilter.END -> festival.festivalDuration.endDate.lt(currentTime)
        }
    }

    private fun applyEqRegion(region: SchoolRegion): BooleanExpression? {
        return if (region != SchoolRegion.ANY) school.region.eq(region) else null
    }

    private fun dynamicOrderBy(filter: FestivalFilter): Array<OrderSpecifier<*>> {
        return when (filter) {
            FestivalFilter.PLANNED -> arrayOf(
                festival.festivalDuration.startDate.asc(),
                festival.id.asc()
            )

            FestivalFilter.PROGRESS -> arrayOf(
                festival.festivalDuration.startDate.desc(),
                festival.id.asc()
            )

            FestivalFilter.END -> arrayOf(festival.festivalDuration.endDate.desc())
        }
    }
}
