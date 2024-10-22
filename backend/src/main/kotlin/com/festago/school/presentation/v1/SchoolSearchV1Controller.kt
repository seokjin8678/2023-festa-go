package com.festago.school.presentation.v1

import com.festago.common.util.Validator.minLength
import com.festago.common.util.Validator.notBlank
import com.festago.school.application.v1.SchoolTotalSearchV1QueryService
import com.festago.school.dto.v1.SchoolTotalSearchV1Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search/schools")
@Tag(name = "학교 검색 요청 V1")
class SchoolSearchV1Controller(
    private val schoolTotalSearchV1QueryService: SchoolTotalSearchV1QueryService,
) {

    @GetMapping
    @Operation(description = "키워드로 학교를 검색한다.", summary = "학교 검색")
    fun searchSchools(
        @RequestParam keyword: String,
    ): ResponseEntity<List<SchoolTotalSearchV1Response>> {
        validate(keyword)
        return ResponseEntity.ok()
            .body(schoolTotalSearchV1QueryService.searchSchools(keyword))
    }

    private fun validate(keyword: String) {
        notBlank(keyword, "keyword")
        minLength(keyword, 2, "keyword")
    }
}
