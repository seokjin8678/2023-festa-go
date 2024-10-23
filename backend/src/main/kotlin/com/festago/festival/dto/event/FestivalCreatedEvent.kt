package com.festago.festival.dto.event

import com.festago.festival.domain.Festival

data class FestivalCreatedEvent(
    val festival: Festival,
)
