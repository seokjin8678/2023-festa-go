package com.festago.admin.presentation.v1

import com.festago.admin.application.AdminQueryInfoRenewalService
import com.festago.admin.dto.queryinfo.QueryInfoRenewalFestivalPeriodV1Request
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/v1/query-info/renewal")
class AdminQueryInfoRenewalV1Controller(
    private val queryInfoRenewalService: AdminQueryInfoRenewalService,
) {

    @PostMapping("/festival-id/{festivalId}")
    fun renewalByFestivalId(
        @PathVariable festivalId: Long,
    ): ResponseEntity<Void> {
        queryInfoRenewalService.renewalByFestivalId(festivalId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/festival-period")
    fun renewalByFestivalStartDatePeriod(
        @Valid @RequestBody request: QueryInfoRenewalFestivalPeriodV1Request,
    ): ResponseEntity<Void> {
        queryInfoRenewalService.renewalByFestivalStartDatePeriod(request.to, request.end)
        return ResponseEntity.ok().build()
    }
}
