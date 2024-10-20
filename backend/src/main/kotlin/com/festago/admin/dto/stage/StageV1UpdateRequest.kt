package com.festago.admin.dto.stage

import com.festago.stage.dto.command.StageUpdateCommand
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import org.springframework.format.annotation.DateTimeFormat

data class StageV1UpdateRequest(
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val startTime: LocalDateTime,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val ticketOpenTime: LocalDateTime,
    @field:NotNull
    val artistIds: List<Long>,
) {
    fun toCommand(): StageUpdateCommand {
        return StageUpdateCommand(startTime, ticketOpenTime, artistIds)
    }
}
