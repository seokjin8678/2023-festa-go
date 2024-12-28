package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.Stage
import com.festago.stage.domain.StageRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class StageRepositoryImpl(
    private val stageJpaRepository: StageJpaRepository,
) : StageRepository {

    override fun save(stage: Stage): Stage {
        return stageJpaRepository.save(stage)
    }

    override fun findById(stageId: Long): Stage? {
        return stageJpaRepository.findByIdOrNull(stageId)
    }

    override fun deleteById(stageId: Long) {
        return stageJpaRepository.deleteById(stageId)
    }

    override fun existsByFestivalId(festivalId: Long): Boolean {
        return stageJpaRepository.existsByFestivalId(festivalId)
    }

    override fun findAllByFestivalId(festivalId: Long): List<Stage> {
        return stageJpaRepository.findAllByFestivalId(festivalId)
    }

    override fun findByIdWithFetch(id: Long): Stage? {
        return stageJpaRepository.findByIdWithFetch(id)
    }
}
