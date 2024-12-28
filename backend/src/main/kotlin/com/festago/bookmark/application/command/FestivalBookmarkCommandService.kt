package com.festago.bookmark.application.command

import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkRepository
import com.festago.bookmark.domain.BookmarkType
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.domain.FestivalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FestivalBookmarkCommandService(
    private val bookmarkRepository: BookmarkRepository,
    private val festivalRepository: FestivalRepository,
) {

    fun save(festivalId: Long, memberId: Long) {
        validate(festivalId, memberId)
        if (isExistsBookmark(festivalId, memberId)) {
            return
        }
        bookmarkRepository.save(Bookmark(BookmarkType.FESTIVAL, festivalId, memberId))
    }

    private fun validate(festivalId: Long, memberId: Long) {
        if (!festivalRepository.existsById(festivalId)) {
            throw NotFoundException(ErrorCode.FESTIVAL_NOT_FOUND)
        }
        val festivalBookmarkCount = bookmarkRepository.countByMemberIdAndBookmarkType(memberId, BookmarkType.FESTIVAL)
        if (festivalBookmarkCount >= MAX_FESTIVAL_BOOKMARK_COUNT) {
            throw BadRequestException(ErrorCode.BOOKMARK_LIMIT_EXCEEDED)
        }
    }

    private fun isExistsBookmark(festivalId: Long, memberId: Long): Boolean {
        return bookmarkRepository.existsByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = BookmarkType.FESTIVAL,
            memberId = memberId,
            resourceId = festivalId
        )
    }

    fun delete(festivalId: Long, memberId: Long) {
        bookmarkRepository.deleteByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = BookmarkType.FESTIVAL,
            memberId = memberId,
            resourceId = festivalId
        )
    }

    companion object {
        private const val MAX_FESTIVAL_BOOKMARK_COUNT = 12L
    }
}
