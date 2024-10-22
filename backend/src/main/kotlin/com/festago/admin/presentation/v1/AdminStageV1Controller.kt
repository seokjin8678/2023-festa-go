package com.festago.admin.presentation.v1

import com.festago.admin.application.AdminStageV1QueryService
import com.festago.admin.dto.stage.AdminStageV1Response
import com.festago.admin.dto.stage.StageV1CreateRequest
import com.festago.admin.dto.stage.StageV1UpdateRequest
import com.festago.stage.application.command.StageCommandFacadeService
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import java.net.URI
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/v1/stages")
class AdminStageV1Controller(
    private val stageCommandFacadeService: StageCommandFacadeService,
    private val adminStageV1QueryService: AdminStageV1QueryService,
) {
    @GetMapping("/{stageId}")
    fun findById(@PathVariable stageId: Long): ResponseEntity<AdminStageV1Response> {
        return ResponseEntity.ok().body(adminStageV1QueryService.findById(stageId))
    }

    @PostMapping
    fun createStage(@Valid @RequestBody request: StageV1CreateRequest): ResponseEntity<Void> {
        val stageId = stageCommandFacadeService.createStage(request.toCommand())
        return ResponseEntity.created(URI.create("/admin/api/v1/stages/$stageId")).build()
    }

    @PatchMapping("/{stageId}")
    fun updateStage(
        @PathVariable stageId: Long,
        @Valid @RequestBody request: StageV1UpdateRequest,
    ): ResponseEntity<Void> {
        stageCommandFacadeService.updateStage(stageId, request.toCommand())
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{stageId}")
    fun deleteStage(@PathVariable stageId: Long): ResponseEntity<Void> {
        stageCommandFacadeService.deleteStage(stageId)
        return ResponseEntity.noContent().build()
    }
}
