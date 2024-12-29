package com.festago.festival.presentation.v1

import com.festago.common.annotation.LoggingDetail
import com.festago.festival.application.query.PopularFestivalV1QueryService
import com.festago.festival.dto.PopularFestivalsV1Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/popular/festivals")
@Tag(name = "인기 축제 목록 요청 V1")
class PopularFestivalV1Controller(
    private val popularFestivalV1QueryService: PopularFestivalV1QueryService,
) {

    @LoggingDetail(hideResponseBody = true)
    @GetMapping
    @Operation(description = "인기 축제 목록 7개를 반환한다.", summary = "인기 축제 목록 조회")
    fun findPopularFestivals(): ResponseEntity<PopularFestivalsV1Response> {
        return ResponseEntity.ok(popularFestivalV1QueryService.findPopularFestivals())
    }
}
