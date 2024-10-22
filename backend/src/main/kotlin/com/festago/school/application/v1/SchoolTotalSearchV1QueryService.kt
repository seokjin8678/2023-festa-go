package com.festago.school.application.v1

import com.festago.school.dto.v1.SchoolSearchV1Response
import com.festago.school.dto.v1.SchoolTotalSearchV1Response
import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SchoolTotalSearchV1QueryService(
    private val schoolSearchV1QueryService: SchoolSearchV1QueryService,
    private val schoolUpcomingFestivalStartDateV1QueryService: SchoolUpcomingFestivalStartDateV1QueryService,
) {
    fun searchSchools(keyword: String): List<SchoolTotalSearchV1Response> {
        val schoolSearchResponses = schoolSearchV1QueryService.searchSchools(keyword)
        val schoolIds = schoolSearchResponses.map { it.id }
        val schoolIdToUpcomingFestivalStartDate = getSchoolIdToUpcomingFestivalStartDate(schoolIds)
        return schoolSearchResponses.map { schoolSearchResponse: SchoolSearchV1Response ->
            SchoolTotalSearchV1Response(
                schoolSearchResponse.id,
                schoolSearchResponse.name,
                schoolSearchResponse.logoUrl,
                schoolIdToUpcomingFestivalStartDate[schoolSearchResponse.id]
            )
        }
    }

    private fun getSchoolIdToUpcomingFestivalStartDate(schoolIds: List<Long>): Map<Long, LocalDate> {
        return schoolUpcomingFestivalStartDateV1QueryService.getSchoolIdToUpcomingFestivalStartDate(schoolIds)
    }
}
