package com.festago.school.dto.v1

import com.fasterxml.jackson.annotation.JsonRawValue
import com.festago.artist.domain.ArtistQueryModel
import com.querydsl.core.annotations.QueryProjection
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class SchoolFestivalV1Response @QueryProjection constructor(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val posterImageUrl: String,
    @field:JsonRawValue
    @field:ArraySchema(schema = Schema(implementation = ArtistQueryModel::class))
    val artists: String,
)
