package com.festago.bookmark.presentation.v1

import com.festago.auth.annotation.MemberAuth
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.bookmark.application.FestivalBookmarkV1QueryService
import com.festago.bookmark.dto.v1.FestivalBookmarkV1Response
import com.festago.bookmark.infrastructure.repository.query.FestivalBookmarkOrder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bookmarks/festivals")
@Tag(name = "축제 북마크 요청 V1")
class FestivalBookmarkV1Controller(
    private val festivalBookmarkV1QueryService: FestivalBookmarkV1QueryService,
) {

    @MemberAuth
    @GetMapping("/ids")
    @Operation(description = "회원의 북마크 된 축제 식별자 목록을 조회한다.", summary = "북마크 된 축제 식별자 목록 조회")
    fun findBookmarkedFestivalIds(
        memberAuthentication: MemberAuthentication,
    ): ResponseEntity<List<Long>> {
        return ResponseEntity.ok()
            .body(festivalBookmarkV1QueryService.findBookmarkedFestivalIds(memberAuthentication.memberId))
    }

    @MemberAuth
    @GetMapping
    @Operation(description = "축제 식별자 목록으로 회원의 북마크 된 축제의 목록을 조회한다.", summary = "축제 식별자 목록으로 북마크 된 축제의 목록 조회")
    fun findBookmarkedFestivals(
        memberAuthentication: MemberAuthentication,
        @RequestParam(required = false, defaultValue = "") festivalIds: List<Long>,
        @RequestParam festivalBookmarkOrder: FestivalBookmarkOrder,
    ): ResponseEntity<List<FestivalBookmarkV1Response>> {
        return ResponseEntity.ok()
            .body(
                festivalBookmarkV1QueryService.findBookmarkedFestivals(
                    memberAuthentication.memberId,
                    festivalIds,
                    festivalBookmarkOrder
                )
            )
    }
}
