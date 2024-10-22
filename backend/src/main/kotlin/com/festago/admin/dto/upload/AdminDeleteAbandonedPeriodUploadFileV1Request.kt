package com.festago.admin.dto.upload

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class AdminDeleteAbandonedPeriodUploadFileV1Request(
    @field:NotNull val startTime: LocalDateTime,
    @field:NotNull val endTime: LocalDateTime,
)
