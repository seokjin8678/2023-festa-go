package com.festago.bookmark.infrastructure.repository.query.v1

import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.domain.QBookmark.bookmark
import com.festago.bookmark.dto.v1.FestivalBookmarkV1Response
import com.festago.bookmark.dto.v1.QFestivalBookmarkV1Response
import com.festago.bookmark.infrastructure.repository.query.FestivalBookmarkOrder
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.festival.dto.QFestivalV1Response
import com.festago.festival.dto.QSchoolV1Response
import com.festago.school.domain.QSchool.school
import com.querydsl.core.types.OrderSpecifier
import org.springframework.stereotype.Repository

@Repository
class FestivalBookmarkV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findBookmarkedFestivalIds(memberId: Long): List<Long> {
        return queryDslHelper.select(bookmark.resourceId)
            .from(bookmark)
            .where(
                bookmark.memberId.eq(memberId).and(bookmark.bookmarkType.eq(BookmarkType.FESTIVAL))
            )
            .fetch()
    }

    fun findBookmarkedFestivals(
        memberId: Long,
        festivalIds: List<Long>,
        festivalBookmarkOrder: FestivalBookmarkOrder,
    ): List<FestivalBookmarkV1Response> {
        return queryDslHelper.select(
            QFestivalBookmarkV1Response(
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
                ),
                bookmark.createdAt
            )
        )
            .from(festival)
            .innerJoin(school).on(school.id.eq(festival.school.id))
            .innerJoin(festivalQueryInfo).on(festivalQueryInfo.festivalId.eq(festival.id))
            .innerJoin(bookmark).on(
                bookmark.bookmarkType.eq(BookmarkType.FESTIVAL)
                    .and(bookmark.resourceId.eq(festival.id))
                    .and(bookmark.memberId.eq(memberId))
            )
            .where(festival.id.`in`(festivalIds))
            .orderBy(dynamicOrder(festivalBookmarkOrder))
            .fetch()
    }

    private fun dynamicOrder(festivalBookmarkOrder: FestivalBookmarkOrder): OrderSpecifier<*> {
        return when (festivalBookmarkOrder) {
            FestivalBookmarkOrder.BOOKMARK -> bookmark.id.desc()
            FestivalBookmarkOrder.FESTIVAL -> festival.festivalDuration.startDate.asc()
        }
    }
}
