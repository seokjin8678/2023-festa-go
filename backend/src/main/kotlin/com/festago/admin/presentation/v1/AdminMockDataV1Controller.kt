package com.festago.admin.presentation.v1

import com.festago.mock.application.MockDataService
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@Profile("!prod")
@RestController
@RequestMapping("/admin/api/v1/mock-data")
class AdminMockDataV1Controller(
    private val mockDataService: MockDataService,
) {

    @PostMapping("/festivals")
    fun generateMockFestivals(): ResponseEntity<Void> {
        mockDataService.makeMockFestivals()
        return ResponseEntity.ok().build()
    }
}
