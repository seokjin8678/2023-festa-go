package com.festago.school.application.v1

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.dto.v1.SchoolDetailV1Response
import com.festago.school.dto.v1.SchoolFestivalV1Response
import com.festago.school.infrastructure.repository.query.v1.SchoolFestivalV1SearchCondition
import com.festago.school.infrastructure.repository.query.v1.SchoolV1QueryDslRepository
import java.time.LocalDate
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SchoolV1QueryService(
    private val schoolV1QueryDslRepository: SchoolV1QueryDslRepository,
) {

    fun findDetailById(schoolId: Long): SchoolDetailV1Response {
        return schoolV1QueryDslRepository.findDetailById(schoolId)
            ?: throw NotFoundException(ErrorCode.SCHOOL_NOT_FOUND)
    }

    fun findFestivalsBySchoolId(
        schoolId: Long,
        today: LocalDate,
        searchCondition: SchoolFestivalV1SearchCondition,
    ): Slice<SchoolFestivalV1Response> {
        return schoolV1QueryDslRepository.findFestivalsBySchoolId(schoolId, today, searchCondition)
    }
}
