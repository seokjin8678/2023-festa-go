package com.festago.stage.application.command

import com.festago.stage.dto.event.StageDeletedEvent
import com.festago.stage.repository.StageRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StageDeleteService(
    private val stageRepository: StageRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun deleteStage(stageId: Long) {
        val stage = stageRepository.findById(stageId) ?: return
        stageRepository.deleteById(stageId)
        eventPublisher.publishEvent(StageDeletedEvent(stage))
    }
}
