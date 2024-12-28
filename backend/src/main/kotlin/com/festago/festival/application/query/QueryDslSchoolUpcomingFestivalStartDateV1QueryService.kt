package com.festago.festival.application.query

import com.festago.festival.infrastructure.repository.query.RecentSchoolFestivalV1QueryDslRepository
import com.festago.school.application.v1.SchoolUpcomingFestivalStartDateV1QueryService
import java.time.Clock
import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueryDslSchoolUpcomingFestivalStartDateV1QueryService(
    private val recentSchoolFestivalV1QueryDslRepository: RecentSchoolFestivalV1QueryDslRepository,
    private val clock: Clock,
) : SchoolUpcomingFestivalStartDateV1QueryService {

    override fun getSchoolIdToUpcomingFestivalStartDate(schoolIds: List<Long>): Map<Long, LocalDate> {
        return recentSchoolFestivalV1QueryDslRepository.findRecentSchoolFestivals(schoolIds, LocalDate.now(clock))
            .associateBy({ it.schoolId }, { it.startDate })
    }
}
