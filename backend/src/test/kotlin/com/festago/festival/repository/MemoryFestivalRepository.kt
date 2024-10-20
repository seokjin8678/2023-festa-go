package com.festago.festival.repository

import com.festago.festival.domain.Festival
import com.festago.support.AbstractMemoryRepositoryKt

class MemoryFestivalRepository : AbstractMemoryRepositoryKt<Festival>(), FestivalRepository {

    override fun existsBySchoolId(schoolId: Long): Boolean {
        return memory.values.any { it.school.id == schoolId }
    }
}