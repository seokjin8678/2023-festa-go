package com.festago.bookmark.domain

interface BookmarkRepository {

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
