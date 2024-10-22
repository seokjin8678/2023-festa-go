package com.festago.bookmark.presentation.v1

import com.festago.auth.annotation.MemberAuth
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.bookmark.application.ArtistBookmarkV1QueryService
import com.festago.bookmark.dto.v1.ArtistBookmarkV1Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bookmarks/artists")
@Tag(name = "아티스트 북마크 요청 V1")
class ArtistBookmarkV1Controller(
    private val artistBookmarkV1QueryService: ArtistBookmarkV1QueryService,
) {

    @MemberAuth
    @GetMapping
    @Operation(description = "회원의 아티스트 북마크 목록을 조회한다.", summary = "아티스트 북마크 조회")
    fun findArtistBookmarksByMemberId(
        memberAuthentication: MemberAuthentication,
    ): ResponseEntity<List<ArtistBookmarkV1Response>> {
        return ResponseEntity.ok(artistBookmarkV1QueryService.findArtistBookmarksByMemberId(memberAuthentication.id))
    }
}
