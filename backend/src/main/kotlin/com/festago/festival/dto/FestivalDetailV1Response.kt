package com.festago.festival.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class FestivalDetailV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val school: SchoolV1Response,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val posterImageUrl: String,
    val socialMedias: Set<SocialMediaV1Response>,
    val stages: Set<FestivalStageV1Response>,
)
