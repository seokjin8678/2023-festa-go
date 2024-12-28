package com.festago.artist.application

import com.festago.artist.dto.ArtistSearchStageCountV1Response
import com.festago.artist.infrastructure.repository.query.ArtistSearchV1QueryDslRepository
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArtistSearchStageCountV1QueryService(
    private val artistSearchV1QueryDslRepository: ArtistSearchV1QueryDslRepository,
) {

    fun findArtistsStageCountAfterDateTime(
        artistIds: List<Long>,
        now: LocalDateTime,
    ): Map<Long, ArtistSearchStageCountV1Response> {
        val artistToStageStartTimes = artistSearchV1QueryDslRepository.findArtistsStageScheduleAfterStageStartTime(
            artistIds, now
        )
        val today = now.toLocalDate()
        return artistIds.associateBy({ it }, { artistToStageStartTimes.getOrDefault(it, emptyList()) })
            .mapValuesTo(hashMapOf()) { getArtistStageCount(it.value, today) }
    }

    private fun getArtistStageCount(
        stageStartTimes: List<LocalDateTime>,
        today: LocalDate,
    ): ArtistSearchStageCountV1Response {
        val countOfTodayStage = stageStartTimes.count { it.toLocalDate() == today }
        val countOfPlannedStage = stageStartTimes.size - countOfTodayStage
        return ArtistSearchStageCountV1Response(countOfTodayStage, countOfPlannedStage)
    }
}
