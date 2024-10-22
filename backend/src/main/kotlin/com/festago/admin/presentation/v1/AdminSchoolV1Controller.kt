package com.festago.admin.presentation.v1

import com.festago.admin.application.AdminSchoolV1QueryService
import com.festago.admin.dto.school.AdminSchoolV1Response
import com.festago.admin.dto.school.SchoolV1CreateRequest
import com.festago.admin.dto.school.SchoolV1UpdateRequest
import com.festago.common.aop.ValidPageable
import com.festago.common.querydsl.SearchCondition
import com.festago.school.application.SchoolCommandService
import com.festago.school.application.SchoolDeleteService
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
@RequestMapping("/admin/api/v1/schools")
class AdminSchoolV1Controller(
    private val schoolCommandService: SchoolCommandService,
    private val schoolDeleteService: SchoolDeleteService,
    private val schoolQueryService: AdminSchoolV1QueryService,
) {

    @PostMapping
    fun createSchool(@Valid @RequestBody request: SchoolV1CreateRequest): ResponseEntity<Void> {
        val schoolId = schoolCommandService.createSchool(request.toCommand())
        return ResponseEntity.created(URI.create("/api/v1/schools/$schoolId"))
            .build()
    }

    @PatchMapping("/{schoolId}")
    fun updateSchool(
        @PathVariable schoolId: Long,
        @Valid @RequestBody request: SchoolV1UpdateRequest,
    ): ResponseEntity<Void> {
        schoolCommandService.updateSchool(schoolId, request.toCommand())
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{schoolId}")
    fun deleteSchool(@PathVariable schoolId: Long): ResponseEntity<Void> {
        schoolDeleteService.deleteSchool(schoolId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping
    @ValidPageable(maxSize = 50)
    fun findAllSchools(
        @RequestParam searchFilter: String = "",
        @RequestParam searchKeyword: String = "",
        @PageableDefault(size = 10) pageable: Pageable,
    ): ResponseEntity<Page<AdminSchoolV1Response>> {
        return ResponseEntity.ok()
            .body(schoolQueryService.findAll(SearchCondition(searchFilter, searchKeyword, pageable)))
    }

    @GetMapping("/{schoolId}")
    fun findSchoolById(@PathVariable schoolId: Long): ResponseEntity<AdminSchoolV1Response> {
        return ResponseEntity.ok()
            .body(schoolQueryService.findById(schoolId))
    }
}
