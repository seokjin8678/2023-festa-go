package com.festago.festival.infrastructure.repository

import com.festago.festival.domain.FestivalQueryInfo
import com.festago.festival.domain.FestivalQueryInfoRepository
import com.festago.support.AbstractMemoryRepository

class MemoryFestivalQueryInfoRepository : AbstractMemoryRepository<FestivalQueryInfo>(), FestivalQueryInfoRepository {

    override fun findByFestivalId(festivalId: Long): FestivalQueryInfo? {
        return memory.values.find { it.id == festivalId }
    }

    override fun deleteByFestivalId(festivalId: Long) {
        memory.entries.removeIf { it.value.festivalId == festivalId }
    }
}
