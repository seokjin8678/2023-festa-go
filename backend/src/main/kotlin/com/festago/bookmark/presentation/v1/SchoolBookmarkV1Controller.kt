package com.festago.bookmark.presentation.v1

import com.festago.auth.annotation.MemberAuth
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.bookmark.application.SchoolBookmarkV1QueryService
import com.festago.bookmark.dto.v1.SchoolBookmarkV1Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bookmarks/schools")
@Tag(name = "학교 북마크 요청 V1")
class SchoolBookmarkV1Controller(
    private val schoolBookmarkV1QueryService: SchoolBookmarkV1QueryService,
) {

    @MemberAuth
    @GetMapping
    @Operation(description = "회원의 학교 북마크 목록을 조회한다", summary = "학교 북마크 조회")
    fun findAllByMemberId(
        memberAuthentication: MemberAuthentication,
    ): ResponseEntity<List<SchoolBookmarkV1Response>> {
        return ResponseEntity.ok(schoolBookmarkV1QueryService.findAllByMemberId(memberAuthentication.id))
    }
}
