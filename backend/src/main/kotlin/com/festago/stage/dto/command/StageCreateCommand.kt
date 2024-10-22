package com.festago.stage.dto.command

import java.time.LocalDateTime

data class StageCreateCommand(
    val festivalId: Long,
    val startTime: LocalDateTime,
    val ticketOpenTime: LocalDateTime,
    val artistIds: List<Long>,
)
