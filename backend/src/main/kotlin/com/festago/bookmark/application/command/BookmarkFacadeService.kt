package com.festago.bookmark.application.command

import com.festago.bookmark.domain.BookmarkType
import org.springframework.stereotype.Service

@Service
class BookmarkFacadeService(
    private val schoolBookmarkCommandService: SchoolBookmarkCommandService,
    private val artistBookmarkCommandService: ArtistBookmarkCommandService,
    private val festivalBookmarkCommandService: FestivalBookmarkCommandService,
) {

    fun save(
        bookmarkType: BookmarkType,
        resourceId: Long,
        memberId: Long,
    ) {
        when (bookmarkType) {
            BookmarkType.SCHOOL -> schoolBookmarkCommandService.save(resourceId, memberId)
            BookmarkType.ARTIST -> artistBookmarkCommandService.save(resourceId, memberId)
            BookmarkType.FESTIVAL -> festivalBookmarkCommandService.save(resourceId, memberId)
        }
    }

    fun delete(
        bookmarkType: BookmarkType,
        resourceId: Long,
        memberId: Long,
    ) {
        when (bookmarkType) {
            BookmarkType.SCHOOL -> schoolBookmarkCommandService.delete(resourceId, memberId)
            BookmarkType.ARTIST -> artistBookmarkCommandService.delete(resourceId, memberId)
            BookmarkType.FESTIVAL -> festivalBookmarkCommandService.delete(resourceId, memberId)
        }
    }
}
