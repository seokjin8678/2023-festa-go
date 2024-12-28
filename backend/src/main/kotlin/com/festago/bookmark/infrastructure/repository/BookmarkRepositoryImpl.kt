package com.festago.bookmark.infrastructure.repository

import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkRepository
import com.festago.bookmark.domain.BookmarkType
import org.springframework.stereotype.Repository

@Repository
internal class BookmarkRepositoryImpl(
    private val bookmarkJpaRepository: BookmarkJpaRepository,
) : BookmarkRepository {

    override fun save(bookmark: Bookmark): Bookmark {
        return bookmarkJpaRepository.save(bookmark)
    }

    override fun deleteById(id: Long) {
        bookmarkJpaRepository.deleteById(id)
    }

    override fun existsByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ): Boolean {
        return bookmarkJpaRepository.existsByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = bookmarkType,
            memberId = memberId,
            resourceId = resourceId,
        )
    }

    override fun countByMemberIdAndBookmarkType(memberId: Long, bookmarkType: BookmarkType): Long {
        return bookmarkJpaRepository.countByMemberIdAndBookmarkType(memberId, bookmarkType)
    }

    override fun deleteByBookmarkTypeAndMemberIdAndResourceId(
        bookmarkType: BookmarkType,
        memberId: Long,
        resourceId: Long,
    ) {
        return bookmarkJpaRepository.deleteByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = bookmarkType,
            memberId = memberId,
            resourceId = resourceId
        )
    }
}
