package com.festago.festival.dto

import com.fasterxml.jackson.annotation.JsonRawValue
import com.festago.artist.infrastructure.JsonArtistsSerializer.ArtistQueryModel
import com.querydsl.core.annotations.QueryProjection
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class FestivalStageV1Response @QueryProjection constructor(
    val id: Long,
    val startDateTime: LocalDateTime,
    @field:ArraySchema(schema = Schema(implementation = ArtistQueryModel::class))
    @field:JsonRawValue
    val artists: String?
)
