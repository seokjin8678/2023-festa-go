package com.festago.stage.dto.event

import com.festago.stage.domain.Stage

data class StageDeletedEvent(
    val stage: Stage,
)
