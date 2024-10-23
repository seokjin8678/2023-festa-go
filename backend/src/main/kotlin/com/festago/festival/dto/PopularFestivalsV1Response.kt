package com.festago.festival.dto

data class PopularFestivalsV1Response(
    val title: String,
    val content: List<FestivalV1Response>,
)
