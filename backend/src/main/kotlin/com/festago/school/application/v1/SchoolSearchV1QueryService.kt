package com.festago.school.application.v1

import com.festago.school.dto.v1.SchoolSearchV1Response
import com.festago.school.infrastructure.repository.query.v1.SchoolSearchV1QueryDslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SchoolSearchV1QueryService(
    private val schoolSearchV1QueryDslRepository: SchoolSearchV1QueryDslRepository,
) {

    fun searchSchools(keyword: String): List<SchoolSearchV1Response> {
        return schoolSearchV1QueryDslRepository.searchSchools(keyword)
    }
}
