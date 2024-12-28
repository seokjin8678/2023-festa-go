package com.festago.bookmark.infrastructure.repository

import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkType
import org.springframework.data.jpa.repository.JpaRepository

internal interface BookmarkJpaRepository : JpaRepository<Bookmark, Long> {

    fun existsByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ): Boolean

    fun countByMemberIdAndBookmarkType(memberId: Long, bookmarkType: BookmarkType): Long

    fun deleteByBookmarkTypeAndMemberIdAndResourceId(bookmarkType: BookmarkType, memberId: Long, resourceId: Long)
}
