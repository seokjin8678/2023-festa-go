package com.festago.admin.dto.queryinfo

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class QueryInfoRenewalFestivalPeriodV1Request(
    @field:NotNull
    val to: LocalDate,
    @field:NotNull
    val end: LocalDate,
)
