package com.festago.artist.presentation.v1

import com.festago.artist.application.ArtistTotalSearchV1Service
import com.festago.artist.dto.ArtistTotalSearchV1Response
import com.festago.common.util.Validator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search/artists")
@Tag(name = "아티스트 검색 요청 V1")
class ArtistSearchV1Controller(
    private val artistTotalSearchV1Service: ArtistTotalSearchV1Service,
) {

    @GetMapping
    @Operation(description = "키워드로 아티스트 목록을 검색한다.", summary = "아티스트 검색")
    fun searchByKeyword(@RequestParam keyword: String): ResponseEntity<List<ArtistTotalSearchV1Response>> {
        Validator.notBlank(keyword, "keyword")
        return ResponseEntity.ok(artistTotalSearchV1Service.findAllByKeyword(keyword))
    }
}
