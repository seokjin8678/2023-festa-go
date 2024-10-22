package com.festago.admin.presentation.v1

import com.festago.admin.application.AdminSocialMediaV1QueryService
import com.festago.admin.dto.socialmedia.AdminSocialMediaV1Response
import com.festago.admin.dto.socialmedia.SocialMediaCreateV1Request
import com.festago.admin.dto.socialmedia.SocialMediaUpdateV1Request
import com.festago.socialmedia.application.SocialMediaCommandService
import com.festago.socialmedia.domain.OwnerType
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
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
@RequestMapping("/admin/api/v1/socialmedias")
class AdminSocialMediaV1Controller(
    private val socialMediaCommandService: SocialMediaCommandService,
    private val adminSocialMediaV1QueryService: AdminSocialMediaV1QueryService,
) {

    @GetMapping
    fun findByOwnerIdAndOwnerType(
        @RequestParam ownerId: Long,
        @RequestParam ownerType: OwnerType,
    ): ResponseEntity<List<AdminSocialMediaV1Response>> {
        return ResponseEntity.ok()
            .body(adminSocialMediaV1QueryService.findByOwnerIdAndOwnerType(ownerId, ownerType))
    }

    @GetMapping("/{socialMediaId}")
    fun findById(@PathVariable socialMediaId: Long): ResponseEntity<AdminSocialMediaV1Response> {
        return ResponseEntity.ok()
            .body(adminSocialMediaV1QueryService.findById(socialMediaId))
    }

    @PostMapping
    fun createSocialMedia(@Valid @RequestBody request: SocialMediaCreateV1Request): ResponseEntity<Void> {
        socialMediaCommandService.createSocialMedia(request.toCommand())
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/{socialMediaId}")
    fun updateSocialMedia(
        @PathVariable socialMediaId: Long,
        @Valid @RequestBody request: SocialMediaUpdateV1Request,
    ): ResponseEntity<Void> {
        socialMediaCommandService.updateSocialMedia(socialMediaId, request.toCommand())
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{socialMediaId}")
    fun deleteSocialMedia(@PathVariable socialMediaId: Long): ResponseEntity<Void> {
        socialMediaCommandService.deleteSocialMedia(socialMediaId)
        return ResponseEntity.noContent().build()
    }
}
