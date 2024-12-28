package com.festago.school.application

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRepository
import com.festago.school.domain.getOrThrow
import com.festago.school.dto.command.SchoolCreateCommand
import com.festago.school.dto.command.SchoolUpdateCommand
import com.festago.school.dto.event.SchoolCreatedEvent
import com.festago.school.dto.event.SchoolUpdatedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SchoolCommandService(
    private val schoolRepository: SchoolRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun createSchool(command: SchoolCreateCommand): Long {
        validateCreate(command)
        val school = schoolRepository.save(command.toEntity())
        eventPublisher.publishEvent(SchoolCreatedEvent(school))
        return school.identifier
    }

    private fun validateCreate(command: SchoolCreateCommand) {
        val name = command.name
        if (schoolRepository.existsByName(name)) {
            throw BadRequestException(ErrorCode.DUPLICATE_SCHOOL_NAME)
        }
    }

    fun updateSchool(schoolId: Long, command: SchoolUpdateCommand) {
        val school = schoolRepository.getOrThrow(schoolId)
        validateUpdate(school, command)
        school.changeName(command.name)
        school.changeDomain(command.domain)
        school.changeRegion(command.region)
        school.changeLogoUrl(command.logoUrl)
        school.changeBackgroundImageUrl(command.backgroundImageUrl)
        eventPublisher.publishEvent(SchoolUpdatedEvent(school))
    }

    private fun validateUpdate(school: School, command: SchoolUpdateCommand) {
        val name = command.name
        if (school.name != name && schoolRepository.existsByName(name)) {
            throw BadRequestException(ErrorCode.DUPLICATE_SCHOOL_NAME)
        }
    }
}
