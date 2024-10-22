package com.festago.festival.application.command

import com.festago.festival.domain.validator.FestivalDeleteValidator
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.repository.FestivalRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FestivalDeleteService(
    private val festivalRepository: FestivalRepository,
    private val validators: List<FestivalDeleteValidator>,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun deleteFestival(festivalId: Long) {
        validators.forEach { it.validate(festivalId) }
        festivalRepository.deleteById(festivalId)
        eventPublisher.publishEvent(FestivalDeletedEvent(festivalId))
    }
}
