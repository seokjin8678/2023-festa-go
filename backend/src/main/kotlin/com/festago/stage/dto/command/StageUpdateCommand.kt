package com.festago.stage.dto.command

import java.time.LocalDateTime

data class StageUpdateCommand(
    val startTime: LocalDateTime,
    val ticketOpenTime: LocalDateTime,
    val artistIds: List<Long>,
)
