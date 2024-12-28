package com.festago.admin.application

import com.festago.admin.dto.school.AdminSchoolV1Response
import com.festago.admin.infrastructure.repository.query.AdminSchoolV1QueryDslRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.querydsl.SearchCondition
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminSchoolV1QueryService(
    private val schoolQueryDslRepository: AdminSchoolV1QueryDslRepository,
) {

    fun findAll(searchCondition: SearchCondition): Page<AdminSchoolV1Response> {
        return schoolQueryDslRepository.findAll(searchCondition)
    }

    fun findById(schoolId: Long): AdminSchoolV1Response {
        return schoolQueryDslRepository.findById(schoolId)
            ?: throw NotFoundException(ErrorCode.SCHOOL_NOT_FOUND)
    }
}
