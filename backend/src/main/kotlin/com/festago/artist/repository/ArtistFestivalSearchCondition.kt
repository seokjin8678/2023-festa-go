package com.festago.artist.repository

import java.time.LocalDate
import org.springframework.data.domain.Pageable

data class ArtistFestivalSearchCondition(
    val artistId: Long,
    val isPast: Boolean,
    val lastFestivalId: Long?,
    val lastStartDate: LocalDate?,
    val pageable: Pageable,
    val currentTime: LocalDate,
)
