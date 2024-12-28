package com.festago.festival.presentation.v1

import com.festago.common.aop.ValidPageable
import com.festago.festival.application.query.FestivalDetailV1QueryService
import com.festago.festival.application.query.FestivalV1QueryService
import com.festago.festival.dto.FestivalDetailV1Response
import com.festago.festival.dto.FestivalV1QueryRequest
import com.festago.festival.dto.FestivalV1Response
import com.festago.festival.infrastructure.repository.query.FestivalFilter
import com.festago.school.domain.SchoolRegion
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/festivals")
@Tag(name = "축제 정보 요청 V1")
class FestivalV1Controller(
    private val festivalV1QueryService: FestivalV1QueryService,
    private val festivalDetailV1QueryService: FestivalDetailV1QueryService,
) {

    @GetMapping
    @ValidPageable(maxSize = 20)
    @Operation(description = "축제 목록를 조건별로 조회한다.", summary = "축제 목록 조회")
    fun findFestivals(
        @Parameter(description = "0 < size <= 20")
        @RequestParam(defaultValue = "10")
        size: Int,
        @RequestParam(defaultValue = "ANY")
        region: SchoolRegion,
        @Parameter(description = "PROGRESS: 진행 중, PLANNED: 진행 예정, END: 종료")
        @RequestParam(defaultValue = "PROGRESS")
        filter: FestivalFilter,
        @RequestParam(required = false)
        lastFestivalId: Long?,
        @RequestParam(required = false)
        lastStartDate: LocalDate?,
    ): ResponseEntity<Slice<FestivalV1Response>> {
        val request = FestivalV1QueryRequest(region, filter, lastFestivalId, lastStartDate)
        val response = festivalV1QueryService.findFestivals(PageRequest.ofSize(size), request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{festivalId}")
    @Operation(description = "축제의 정보를 조회한다.", summary = "축제 정보 조회")
    fun findFestivalDetail(
        @PathVariable festivalId: Long,
    ): ResponseEntity<FestivalDetailV1Response> {
        val response = festivalDetailV1QueryService.findFestivalDetail(festivalId)
        return ResponseEntity.ok(response)
    }
}
