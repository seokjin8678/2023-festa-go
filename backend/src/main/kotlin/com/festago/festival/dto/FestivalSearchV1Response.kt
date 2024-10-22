package com.festago.festival.dto

import com.fasterxml.jackson.annotation.JsonRawValue
import com.festago.artist.domain.ArtistQueryModel
import com.querydsl.core.annotations.QueryProjection
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class FestivalSearchV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val posterImageUrl: String,
    @field:ArraySchema(schema = Schema(implementation = ArtistQueryModel::class))
    @field:JsonRawValue
    val artists: String
)
