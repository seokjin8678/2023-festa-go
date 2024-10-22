package com.festago.stage.dto.event

import com.festago.stage.domain.Stage

data class StageUpdatedEvent(
    val stage: Stage,
)
