package com.festago.festival.application.command

import com.festago.festival.domain.FestivalDuration
import com.festago.festival.domain.validator.FestivalUpdateValidator
import com.festago.festival.dto.command.FestivalUpdateCommand
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.festival.repository.FestivalRepository
import com.festago.festival.repository.getOrThrow
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FestivalUpdateService(
    private val festivalRepository: FestivalRepository,
    private val validators: List<FestivalUpdateValidator>,
    private val eventPublisher: ApplicationEventPublisher,
) {

    /**
     * 강제로 수정할 일이 필요할 수 있으므로, 시작일이 과거여도 예외를 발생하지 않음
     */
    fun updateFestival(festivalId: Long, command: FestivalUpdateCommand) {
        val festival = festivalRepository.getOrThrow(festivalId)
        festival.changeName(command.name)
        festival.changePosterImageUrl(command.posterImageUrl ?: "")
        festival.changeFestivalDuration(FestivalDuration(command.startDate, command.endDate))
        validators.forEach { it.validate(festival) }
        eventPublisher.publishEvent(FestivalUpdatedEvent(festival))
    }
}
