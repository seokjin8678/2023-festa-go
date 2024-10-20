package com.festago.stage.application.command

import com.festago.artist.repository.ArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.util.Validator
import com.festago.festival.repository.FestivalRepository
import com.festago.festival.repository.getOrThrow
import com.festago.stage.domain.Stage
import com.festago.stage.dto.command.StageCreateCommand
import com.festago.stage.dto.event.StageCreatedEvent
import com.festago.stage.repository.StageRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StageCreateService(
    private val stageRepository: StageRepository,
    private val festivalRepository: FestivalRepository,
    private val artistRepository: ArtistRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun createStage(command: StageCreateCommand): Long {
        validate(command)
        val festival = festivalRepository.getOrThrow(command.festivalId)
        val stage = stageRepository.save(
            Stage(
                startTime = command.startTime,
                ticketOpenTime = command.ticketOpenTime,
                festival = festival
            )
        )
        val artistIds = command.artistIds
        stage.renewArtists(artistIds)
        eventPublisher.publishEvent(StageCreatedEvent(stage))
        return stage.identifier
    }

    private fun validate(command: StageCreateCommand) {
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
