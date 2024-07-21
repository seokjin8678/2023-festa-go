package com.festago.festival.domain.validator.school

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.repository.FestivalRepository
import com.festago.school.domain.validator.SchoolDeleteValidator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ExistsFestivalSchoolDeleteValidator(
    private val festivalRepository: FestivalRepository
) : SchoolDeleteValidator {

    override fun validate(schoolId: Long) {
        if (festivalRepository.existsBySchoolId(schoolId)) {
            throw BadRequestException(ErrorCode.SCHOOL_DELETE_CONSTRAINT_EXISTS_FESTIVAL)
        }
    }
}
