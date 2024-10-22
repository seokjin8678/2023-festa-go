package com.festago.bookmark.repository.v1

import com.festago.artist.domain.QArtist.artist
import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.domain.QBookmark.bookmark
import com.festago.bookmark.dto.v1.ArtistBookmarkV1Response
import com.festago.bookmark.dto.v1.QArtistBookmarkInfoV1Response
import com.festago.bookmark.dto.v1.QArtistBookmarkV1Response
import com.festago.common.querydsl.QueryDslHelper
import org.springframework.stereotype.Repository

@Repository
class ArtistBookmarkV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findByMemberId(memberId: Long): List<ArtistBookmarkV1Response> {
        return queryDslHelper.select(
            QArtistBookmarkV1Response(
                QArtistBookmarkInfoV1Response(
                    artist.id,
                    artist.name,
                    artist.profileImage
                ),
                bookmark.createdAt
            )
        )
            .from(bookmark)
            .innerJoin(artist).on(
                bookmark.bookmarkType.eq(BookmarkType.ARTIST)
                    .and(bookmark.memberId.eq(memberId))
                    .and(bookmark.resourceId.eq(artist.id))
            )
            .fetch()
    }
}
