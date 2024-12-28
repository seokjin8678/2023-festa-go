package com.festago.bookmark.application.command

import com.festago.artist.domain.ArtistRepository
import com.festago.bookmark.domain.Bookmark
import com.festago.bookmark.domain.BookmarkRepository
import com.festago.bookmark.domain.BookmarkType
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArtistBookmarkCommandService(
    private val bookmarkRepository: BookmarkRepository,
    private val artistRepository: ArtistRepository,
) {

    fun save(artistId: Long, memberId: Long) {
        validate(artistId, memberId)
        if (isExistsBookmark(artistId, memberId)) {
            return
        }
        bookmarkRepository.save(Bookmark(BookmarkType.ARTIST, artistId, memberId))
    }

    private fun validate(artistId: Long, memberId: Long) {
        validateExistArtist(artistId)
        validateMaxBookmark(memberId)
    }

    private fun validateExistArtist(artistId: Long) {
        if (!artistRepository.existsById(artistId)) {
            throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
        }
    }

    private fun validateMaxBookmark(memberId: Long) {
        val bookmarkCount = bookmarkRepository.countByMemberIdAndBookmarkType(memberId, BookmarkType.ARTIST)
        if (bookmarkCount >= MAX_ARTIST_BOOKMARK_COUNT) {
            throw BadRequestException(ErrorCode.BOOKMARK_LIMIT_EXCEEDED)
        }
    }

    private fun isExistsBookmark(artistId: Long, memberId: Long): Boolean {
        return bookmarkRepository.existsByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = BookmarkType.ARTIST,
            memberId = memberId,
            resourceId = artistId
        )
    }

    fun delete(artistId: Long, memberId: Long) {
        bookmarkRepository.deleteByBookmarkTypeAndMemberIdAndResourceId(
            bookmarkType = BookmarkType.ARTIST,
            memberId = memberId,
            resourceId = artistId
        )
    }

    companion object {
        private const val MAX_ARTIST_BOOKMARK_COUNT = 12L
    }
}
