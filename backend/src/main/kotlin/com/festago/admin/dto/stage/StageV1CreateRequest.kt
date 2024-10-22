package com.festago.admin.dto.stage

import com.festago.stage.dto.command.StageCreateCommand
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import org.springframework.format.annotation.DateTimeFormat

data class StageV1CreateRequest(

    @field:NotNull
    val festivalId: Long,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val startTime: LocalDateTime,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val ticketOpenTime: LocalDateTime,
    @field:NotNull
    val artistIds: List<Long>,
) {
    fun toCommand(): StageCreateCommand {
        return StageCreateCommand(
            festivalId = festivalId,
            startTime = startTime,
            ticketOpenTime = ticketOpenTime,
            artistIds = artistIds
        )
    }
}
