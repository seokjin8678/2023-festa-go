package com.festago.admin.dto.festival

import com.festago.festival.dto.command.FestivalCreateCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat

data class FestivalV1CreateRequest(
    @field:NotBlank
    val name: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate,

    val posterImageUrl: String?,

    @field:NotNull
    val schoolId: Long,
) {
    fun toCommand(): FestivalCreateCommand {
        return FestivalCreateCommand(
            name = name,
            startDate = startDate,
            endDate = endDate,
            posterImageUrl = posterImageUrl,
            schoolId = schoolId
        )
    }
}
