package com.festago.admin.presentation.v1

import com.festago.admin.application.AdminFestivalV1QueryService
import com.festago.admin.application.AdminStageV1QueryService
import com.festago.admin.dto.festival.AdminFestivalDetailV1Response
import com.festago.admin.dto.festival.AdminFestivalV1Response
import com.festago.admin.dto.festival.FestivalV1CreateRequest
import com.festago.admin.dto.festival.FestivalV1UpdateRequest
import com.festago.admin.dto.stage.AdminStageV1Response
import com.festago.common.aop.ValidPageable
import com.festago.common.querydsl.SearchCondition
import com.festago.festival.application.command.FestivalCommandFacadeService
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import java.net.URI
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/v1/festivals")
class AdminFestivalV1Controller(
    private val adminStageV1QueryService: AdminStageV1QueryService,
    private val adminFestivalV1QueryService: AdminFestivalV1QueryService,
    private val festivalCommandFacadeService: FestivalCommandFacadeService,
) {

    @ValidPageable(maxSize = 50)
    @GetMapping
    fun findAll(
        @RequestParam searchFilter: String = "",
        @RequestParam searchKeyword: String = "",
        @PageableDefault(size = 10) pageable: Pageable,
    ): ResponseEntity<Page<AdminFestivalV1Response>> {
        return ResponseEntity.ok()
            .body(adminFestivalV1QueryService.findAll(SearchCondition(searchFilter, searchKeyword, pageable)))
    }

    @GetMapping("/{festivalId}")
    fun findDetail(@PathVariable festivalId: Long): ResponseEntity<AdminFestivalDetailV1Response> {
        return ResponseEntity.ok()
            .body(adminFestivalV1QueryService.findDetail(festivalId))
    }

    @GetMapping("/{festivalId}/stages")
    fun findAllStagesByFestivalId(@PathVariable festivalId: Long): ResponseEntity<List<AdminStageV1Response>> {
        return ResponseEntity.ok()
            .body(adminStageV1QueryService.findAllByFestivalId(festivalId))
    }

    @PostMapping
    fun createFestival(@Valid @RequestBody request: FestivalV1CreateRequest): ResponseEntity<Void> {
        val festivalId = festivalCommandFacadeService.createFestival(request.toCommand())
        return ResponseEntity.created(URI.create("/admin/api/v1/festivals/$festivalId"))
            .build()
    }

    @PatchMapping("/{festivalId}")
    fun updateFestival(
        @PathVariable festivalId: Long,
        @Valid @RequestBody request: FestivalV1UpdateRequest,
    ): ResponseEntity<Void> {
        festivalCommandFacadeService.updateFestival(festivalId, request.toCommand())
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{festivalId}")
    fun deleteFestival(@PathVariable festivalId: Long): ResponseEntity<Void> {
        festivalCommandFacadeService.deleteFestival(festivalId)
        return ResponseEntity.noContent().build()
    }
}
