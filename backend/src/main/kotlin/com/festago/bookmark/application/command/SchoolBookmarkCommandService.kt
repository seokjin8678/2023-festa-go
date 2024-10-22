package com.festago.bookmark.application.command

import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.repository.BookmarkRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.repository.SchoolRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SchoolBookmarkCommandService(
    private val bookmarkRepository: BookmarkRepository,
    private val schoolRepository: SchoolRepository,
) {

    fun save(schoolId: Long, memberId: Long) {
        validate(schoolId, memberId)
        if (isExistsBookmark(schoolId, memberId)) {
            return
        }
        bookmarkRepository.save(Bookmark(BookmarkType.SCHOOL, schoolId, memberId))
    }

    private fun validate(schoolId: Long, memberId: Long) {
        if (!schoolRepository.existsById(schoolId)) {
            throw NotFoundException(ErrorCode.SCHOOL_NOT_FOUND)
        }

        val bookmarkCount = bookmarkRepository.countByMemberIdAndBookmarkType(memberId, BookmarkType.SCHOOL)
        if (bookmarkCount >= MAX_SCHOOL_BOOKMARK_COUNT) {
            throw BadRequestException(ErrorCode.BOOKMARK_LIMIT_EXCEEDED)
        }
    }

    private fun isExistsBookmark(schoolId: Long, memberId: Long): Boolean {
        return bookmarkRepository.existsByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = BookmarkType.SCHOOL,
            memberId = memberId,
            resourceId = schoolId
        )
    }

    fun delete(schoolId: Long, memberId: Long) {
        bookmarkRepository.deleteByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = BookmarkType.SCHOOL,
            memberId = memberId,
            resourceId = schoolId
        )
    }

    companion object {
        private const val MAX_SCHOOL_BOOKMARK_COUNT = 12L
    }
}
