package com.festago.bookmark.repository

import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkType
import com.festago.support.AbstractMemoryRepositoryKt

class MemoryBookmarkRepository : AbstractMemoryRepositoryKt<Bookmark>(), BookmarkRepository {
    override fun existsByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ): Boolean {
        return getByBookmarkTypeAndMemberIdAndResourceId(bookmarkType, memberId, resourceId) != null
    }

    private fun getByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ): Bookmark? {
        return memory.values.asSequence()
            .filter { it.bookmarkType == bookmarkType }
            .filter { it.memberId == memberId }
            .filter { it.resourceId == resourceId }
            .firstOrNull()
    }

    override fun countByMemberIdAndBookmarkType(memberId: Long, bookmarkType: BookmarkType): Long {
        return memory.values.stream()
            .filter { it.memberId == memberId }
            .filter { it.bookmarkType == bookmarkType }
            .count()
    }

    override fun deleteByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ) {
        getByBookmarkTypeAndMemberIdAndResourceId(bookmarkType, memberId, resourceId)?.also {
            memory.remove(it.id)
        }
    }
}
