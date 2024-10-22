package com.festago.admin.presentation.v1

import com.festago.admin.dto.upload.AdminImageUploadV1Response
import com.festago.upload.application.ImageFileUploadService
import com.festago.upload.domain.FileOwnerType
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Hidden
@RestController
@RequestMapping("/admin/api/v1/upload/images")
class AdminUploadImageV1Controller(
    private val imageFileUploadService: ImageFileUploadService,
) {

    @PostMapping
    fun uploadImage(
        @RequestPart image: MultipartFile,
        @RequestParam ownerId: Long?,
        @RequestParam ownerType: FileOwnerType?,
    ): ResponseEntity<AdminImageUploadV1Response> {
        val result = imageFileUploadService.upload(image, ownerId, ownerType)
        return ResponseEntity.ok()
            .body(AdminImageUploadV1Response(result.uploadUri()))
    }
}
