package com.festago.school.application.v1

import java.time.LocalDate

interface SchoolUpcomingFestivalStartDateV1QueryService {
    fun getSchoolIdToUpcomingFestivalStartDate(schoolIds: List<Long>): Map<Long, LocalDate>
}
