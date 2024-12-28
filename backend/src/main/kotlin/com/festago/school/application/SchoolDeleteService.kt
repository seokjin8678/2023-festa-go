package com.festago.school.application

import com.festago.school.domain.SchoolRepository
import com.festago.school.domain.validator.SchoolDeleteValidator
import com.festago.school.dto.event.SchoolDeletedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SchoolDeleteService(
    private val schoolRepository: SchoolRepository,
    private val validators: List<SchoolDeleteValidator>,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun deleteSchool(schoolId: Long) {
        validators.forEach { it.validate(schoolId) }
        schoolRepository.deleteById(schoolId)
        eventPublisher.publishEvent(SchoolDeletedEvent(schoolId))
    }
}
