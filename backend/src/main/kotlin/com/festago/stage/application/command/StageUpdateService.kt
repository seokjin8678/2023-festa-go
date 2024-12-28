package com.festago.stage.application.command

import com.festago.artist.domain.ArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.util.Validator
import com.festago.stage.domain.StageRepository
import com.festago.stage.dto.command.StageUpdateCommand
import com.festago.stage.dto.event.StageUpdatedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StageUpdateService(
    private val stageRepository: StageRepository,
    private val artistRepository: ArtistRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun updateStage(stageId: Long, command: StageUpdateCommand) {
        validate(command)
        val startTime = command.startTime
        val ticketOpenTime = command.ticketOpenTime
        val artistIds = command.artistIds
        val stage = stageRepository.findByIdWithFetch(stageId) ?: throw NotFoundException(ErrorCode.STAGE_NOT_FOUND)
        stage.changeTime(startTime, ticketOpenTime)
        stage.renewArtists(artistIds)
        eventPublisher.publishEvent(StageUpdatedEvent(stage))
    }

    private fun validate(command: StageUpdateCommand) {
        val artistIds = command.artistIds
        Validator.maxSize(artistIds, MAX_ARTIST_SIZE, "artistIds")
        Validator.notDuplicate(artistIds, "artistIds")
        if (artistRepository.countByIdIn(artistIds) != artistIds.size.toLong()) {
            throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
        }
    }

    companion object {
        private const val MAX_ARTIST_SIZE = 10
    }
}
