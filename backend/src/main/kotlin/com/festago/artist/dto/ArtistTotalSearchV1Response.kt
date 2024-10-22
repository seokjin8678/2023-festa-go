package com.festago.artist.dto

data class ArtistTotalSearchV1Response(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
    val todayStage: Int,
    val plannedStage: Int,
) {
    companion object {
        fun of(
            artistResponse: ArtistSearchV1Response,
            stageCount: ArtistSearchStageCountV1Response,
        ): ArtistTotalSearchV1Response {
            return ArtistTotalSearchV1Response(
                artistResponse.id,
                artistResponse.name,
                artistResponse.profileImageUrl,
                stageCount.todayStage,
                stageCount.plannedStage
            )
        }
    }
}
