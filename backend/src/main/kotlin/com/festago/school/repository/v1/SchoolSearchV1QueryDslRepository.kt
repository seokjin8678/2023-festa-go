package com.festago.school.repository.v1

import com.festago.common.querydsl.QueryDslHelper
import com.festago.school.domain.QSchool.school
import com.festago.school.dto.v1.QSchoolSearchV1Response
import com.festago.school.dto.v1.SchoolSearchV1Response
import org.springframework.stereotype.Repository

@Repository
class SchoolSearchV1QueryDslRepository(
    val queryDslHelper: QueryDslHelper,
) {
    fun searchSchools(keyword: String): List<SchoolSearchV1Response> {
        return queryDslHelper.select(
            QSchoolSearchV1Response(
                school.id,
                school.name,
                school.logoUrl
            )
        )
            .from(school)
            .where(school.name.contains(keyword))
            .orderBy(school.name.asc())
            .limit(MAX_FETCH_SIZE)
            .fetch()
    }

    companion object {
        private const val MAX_FETCH_SIZE = 50L
    }
}
