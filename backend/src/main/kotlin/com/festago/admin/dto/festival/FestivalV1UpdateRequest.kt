package com.festago.admin.dto.festival

import com.festago.festival.dto.command.FestivalUpdateCommand
import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat

data class FestivalV1UpdateRequest(
    @field:NotBlank
    val name: String,

    @field:NotNull
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate,

    @field:NotNull
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate,

    @field:Nullable
    val posterImageUrl: String?,
) {
    fun toCommand(): FestivalUpdateCommand {
        return FestivalUpdateCommand(name, startDate, endDate, posterImageUrl)

    }
}
