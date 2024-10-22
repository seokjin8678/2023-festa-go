package com.festago.stage.application.command

import com.festago.stage.dto.command.StageCreateCommand
import com.festago.stage.dto.command.StageUpdateCommand
import org.springframework.stereotype.Service

@Service
class StageCommandFacadeService(
    private val stageCreateService: StageCreateService,
    private val stageUpdateService: StageUpdateService,
    private val stageDeleteService: StageDeleteService,
) {

    fun createStage(command: StageCreateCommand): Long {
        return stageCreateService.createStage(command)
    }

    fun updateStage(stageId: Long, command: StageUpdateCommand) {
        stageUpdateService.updateStage(stageId, command)
    }

    fun deleteStage(stageId: Long) {
        stageDeleteService.deleteStage(stageId)
    }
}
