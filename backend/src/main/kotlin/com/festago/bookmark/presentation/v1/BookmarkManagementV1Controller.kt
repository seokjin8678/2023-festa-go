package com.festago.bookmark.presentation.v1

import com.festago.auth.annotation.MemberAuth
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.bookmark.application.command.BookmarkFacadeService
import com.festago.bookmark.domain.BookmarkType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bookmarks")
@Tag(name = "북마크 등록/삭제 요청 V1")
class BookmarkManagementV1Controller(
    private val bookmarkFacadeService: BookmarkFacadeService,
) {

    @MemberAuth
    @PutMapping
    @Operation(description = "자원의 식별자와 타입으로 북마크를 등록한다.", summary = "북마크 등록")
    fun putBookmark(
        memberAuthentication: MemberAuthentication,
        @RequestParam resourceId: Long,
        @RequestParam bookmarkType: BookmarkType,
    ): ResponseEntity<Void> {
        bookmarkFacadeService.save(
            bookmarkType = bookmarkType,
            resourceId = resourceId,
            memberId = memberAuthentication.memberId
        )
        return ResponseEntity.ok()
            .build()
    }

    @MemberAuth
    @DeleteMapping
    @Operation(description = "자원의 식별자와 타입으로 북마크를 삭제한다.", summary = "북마크 삭제")
    fun deleteBookmark(
        memberAuthentication: MemberAuthentication,
        @RequestParam resourceId: Long,
        @RequestParam bookmarkType: BookmarkType,
    ): ResponseEntity<Void> {
        bookmarkFacadeService.delete(
            bookmarkType = bookmarkType,
            resourceId = resourceId,
            memberId = memberAuthentication.memberId
        )
        return ResponseEntity.noContent()
            .build()
    }
}
