package com.festago.festival.application.command

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalDuration
import com.festago.festival.dto.command.FestivalCreateCommand
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.repository.FestivalRepository
import com.festago.school.repository.SchoolRepository
import java.time.Clock
import java.time.LocalDate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FestivalCreateService(
    private val festivalRepository: FestivalRepository,
    private val schoolRepository: SchoolRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val clock: Clock,
) {

    fun createFestival(command: FestivalCreateCommand): Long {
        val school = schoolRepository.getOrThrow(command.schoolId)
        val festival = Festival(
            name = command.name,
            school = school,
            posterImageUrl = command.posterImageUrl ?: "",
            festivalDuration = FestivalDuration(command.startDate, command.endDate)
        )
        validate(festival)
        festivalRepository.save(festival)
        eventPublisher.publishEvent(FestivalCreatedEvent(festival))
        return festival.id!!
    }

    private fun validate(festival: Festival) {
        if (festival.isStartDateBeforeTo(LocalDate.now(clock))) {
            throw BadRequestException(ErrorCode.INVALID_FESTIVAL_START_DATE)
        }
    }
}
