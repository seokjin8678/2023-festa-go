package com.festago.festival.application.command

import com.festago.festival.dto.command.FestivalCreateCommand
import com.festago.festival.dto.command.FestivalUpdateCommand
import org.springframework.stereotype.Service

@Service
class FestivalCommandFacadeService(
    private val festivalCreateService: FestivalCreateService,
    private val festivalUpdateService: FestivalUpdateService,
    private val festivalDeleteService: FestivalDeleteService,
) {

    fun createFestival(command: FestivalCreateCommand): Long {
        return festivalCreateService.createFestival(command)
    }

    fun updateFestival(festivalId: Long, command: FestivalUpdateCommand) {
        festivalUpdateService.updateFestival(festivalId, command)
    }

    fun deleteFestival(festivalId: Long) {
        festivalDeleteService.deleteFestival(festivalId)
    }
}
