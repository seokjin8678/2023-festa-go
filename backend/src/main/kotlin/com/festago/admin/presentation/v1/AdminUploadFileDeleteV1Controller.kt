package com.festago.admin.presentation.v1

import com.festago.admin.dto.upload.AdminDeleteAbandonedPeriodUploadFileV1Request
import com.festago.upload.application.UploadFileDeleteService
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/v1/upload/delete")
class AdminUploadFileDeleteV1Controller(
    private val uploadFileDeleteService: UploadFileDeleteService,
) {

    @DeleteMapping("/abandoned-period")
    fun deleteAbandonedWithPeriod(
        @Valid @RequestBody request: AdminDeleteAbandonedPeriodUploadFileV1Request,
    ): ResponseEntity<Void> {
        uploadFileDeleteService.deleteAbandonedStatusWithPeriod(request.startTime, request.endTime)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/old-uploaded")
    fun deleteOldUploaded(): ResponseEntity<Void> {
        uploadFileDeleteService.deleteOldUploadedStatus()
        return ResponseEntity.ok().build()
    }
}
