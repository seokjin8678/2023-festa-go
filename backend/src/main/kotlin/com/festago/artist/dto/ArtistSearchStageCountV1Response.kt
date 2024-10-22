package com.festago.artist.dto

data class ArtistSearchStageCountV1Response(
    val todayStage: Int,
    val plannedStage: Int,
) {
    companion object {
        val EMPTY = ArtistSearchStageCountV1Response(0, 0)
    }
}
