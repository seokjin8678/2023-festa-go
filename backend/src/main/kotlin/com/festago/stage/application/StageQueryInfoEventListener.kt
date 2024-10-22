package com.festago.stage.application

import com.festago.stage.dto.event.StageCreatedEvent
import com.festago.stage.dto.event.StageDeletedEvent
import com.festago.stage.dto.event.StageUpdatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class StageQueryInfoEventListener(
    private val stageQueryInfoService: StageQueryInfoService,
) {

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    fun stageCreatedEventHandler(event: StageCreatedEvent) {
        val stage = event.stage
        stageQueryInfoService.initialStageQueryInfo(stage)
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    fun stageUpdatedEventHandler(event: StageUpdatedEvent) {
        val stage = event.stage
        stageQueryInfoService.renewalStageQueryInfo(stage)
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    fun stageDeletedEventHandler(event: StageDeletedEvent) {
        val stage = event.stage
        stageQueryInfoService.deleteStageQueryInfo(stage.identifier)
    }
}
