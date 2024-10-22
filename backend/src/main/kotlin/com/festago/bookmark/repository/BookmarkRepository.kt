package com.festago.bookmark.repository

import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkType
import org.springframework.data.repository.Repository

interface BookmarkRepository : Repository<Bookmark, Long> {
    fun save(bookmark: Bookmark): Bookmark

    fun deleteById(id: Long)

    fun existsByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ): Boolean

    fun countByMemberIdAndBookmarkType(memberId: Long, bookmarkType: BookmarkType): Long

    fun deleteByBookmarkTypeAndMemberIdAndResourceId(bookmarkType: BookmarkType, memberId: Long, resourceId: Long)
}
