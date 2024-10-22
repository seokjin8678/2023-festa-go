package com.festago.bookmark.repository.v1

import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.domain.QBookmark.bookmark
import com.festago.bookmark.dto.v1.QSchoolBookmarkInfoV1Response
import com.festago.bookmark.dto.v1.QSchoolBookmarkV1Response
import com.festago.bookmark.dto.v1.SchoolBookmarkV1Response
import com.festago.common.querydsl.QueryDslHelper
import com.festago.school.domain.QSchool.school
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class SchoolBookmarkV1QuerydslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findAllByMemberId(memberId: Long): List<SchoolBookmarkV1Response> {
        return queryDslHelper.select(
            QSchoolBookmarkV1Response(
                QSchoolBookmarkInfoV1Response(
                    school.id,
                    school.name,
                    school.logoUrl
                ),
                bookmark.createdAt
            )
        )
            .from(bookmark)
            .innerJoin(school).on(
                school.id.eq(bookmark.resourceId)
                    .and(bookmark.memberId.eq(memberId))
                    .and(bookmark.bookmarkType.eq(BookmarkType.SCHOOL))
            )
            .fetch()
    }
}
