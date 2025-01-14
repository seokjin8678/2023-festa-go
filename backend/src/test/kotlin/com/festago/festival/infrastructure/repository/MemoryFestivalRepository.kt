package com.festago.festival.infrastructure.repository

import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalRepository
import com.festago.support.AbstractMemoryRepository

class MemoryFestivalRepository : AbstractMemoryRepository<Festival>(), FestivalRepository {

    override fun existsBySchoolId(schoolId: Long): Boolean {
        return memory.values.any { it.school.id == schoolId }
    }
}
