package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.Stage
import com.festago.stage.domain.StageRepository
import com.festago.support.AbstractMemoryRepository

class MemoryStageRepository : AbstractMemoryRepository<Stage>(), StageRepository {

    override fun existsByFestivalId(festivalId: Long): Boolean {
        return memory.values.any { it.festival.id == festivalId }
    }

    override fun findAllByFestivalId(festivalId: Long): List<Stage> {
        return memory.values.filter { it.festival.id == festivalId }

    }

    override fun findByIdWithFetch(id: Long): Stage? {
        return findById(id)
    }
}
