package com.festago.school.infrastructure.repository.query.v1

import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.school.domain.QSchool.school
import com.festago.school.dto.v1.QSchoolDetailV1Response
import com.festago.school.dto.v1.QSchoolFestivalV1Response
import com.festago.school.dto.v1.QSchoolSocialMediaV1Response
import com.festago.school.dto.v1.SchoolDetailV1Response
import com.festago.school.dto.v1.SchoolFestivalV1Response
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.QSocialMedia.socialMedia
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import java.time.LocalDate
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
class SchoolV1QueryDslRepository(
    val queryDslHelper: QueryDslHelper,
) {
    fun findDetailById(schoolId: Long): SchoolDetailV1Response? {
        val response = queryDslHelper.selectFrom(school)
            .where(school.id.eq(schoolId))
            .leftJoin(socialMedia).on(socialMedia.ownerId.eq(schoolId).and(socialMedia.ownerType.eq(OwnerType.SCHOOL)))
            .transform(
                groupBy(school.id).list(
                    QSchoolDetailV1Response(
                        school.id,
                        school.name,
                        school.logoUrl,
                        school.backgroundUrl,
                        list(
                            QSchoolSocialMediaV1Response(
                                socialMedia.mediaType,
                                socialMedia.name,
                                socialMedia.logoUrl,
                                socialMedia.url
                            ).skipNulls()
                        )
                    )
                )
            )
        return response.firstOrNull()
    }

    fun findFestivalsBySchoolId(
        schoolId: Long,
        today: LocalDate,
        searchCondition: SchoolFestivalV1SearchCondition,
    ): Slice<SchoolFestivalV1Response> {
        val (lastFestivalId, lastStartDate, isPast, pageable) = searchCondition
        return queryDslHelper.applySlice(pageable) {
            it.select(
                QSchoolFestivalV1Response(
                    festival.id,
                    festival.name,
                    festival.festivalDuration.startDate,
                    festival.festivalDuration.endDate,
                    festival.posterImageUrl,
                    festivalQueryInfo.artistInfo
                )
            )
                .from(festival)
                .leftJoin(festivalQueryInfo).on(festivalQueryInfo.festivalId.eq(festival.id))
                .where(
                    festival.school.id.eq(schoolId),
                    addPhaseOption(isPast, today),
                    addPagingOption(lastFestivalId, lastStartDate, isPast)
                )
                .orderBy(addOrderOption(isPast))
        }
    }

    private fun addPhaseOption(isPast: Boolean, today: LocalDate): BooleanExpression {
        if (isPast) {
            return festival.festivalDuration.endDate.lt(today)
        }
        return festival.festivalDuration.endDate.goe(today)
    }

    private fun addPagingOption(lastFestivalId: Long?, lastStartDate: LocalDate?, isPast: Boolean): BooleanExpression? {
        if (isFirstPage(lastFestivalId, lastStartDate)) {
            return null
        }
        if (isPast) {
            return festival.festivalDuration.startDate.lt(lastStartDate)
                .or(
                    festival.festivalDuration.startDate.eq(lastStartDate)
                        .and(festival.id.gt(lastFestivalId))
                )
        }
        return festival.festivalDuration.startDate.gt(lastStartDate)
            .or(
                festival.festivalDuration.startDate.eq(lastStartDate)
                    .and(festival.id.gt(lastFestivalId))
            )
    }

    private fun isFirstPage(lastFestivalId: Long?, lastStartDate: LocalDate?): Boolean {
        return lastFestivalId == null && lastStartDate == null
    }

    private fun addOrderOption(isPast: Boolean): OrderSpecifier<LocalDate> {
        if (isPast) {
            return festival.festivalDuration.endDate.desc()
        }
        return festival.festivalDuration.startDate.asc()
    }
}
