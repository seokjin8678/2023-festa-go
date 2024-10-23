package com.festago.festival.dto.command

import java.time.LocalDate

data class FestivalUpdateCommand(
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val posterImageUrl: String?,
)
