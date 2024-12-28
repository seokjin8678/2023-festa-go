package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.StageQueryInfo
import com.festago.stage.domain.StageQueryInfoRepository
import org.springframework.stereotype.Repository

@Repository
internal class StageQueryInfoRepositoryImpl(
    private val stageQueryInfoJpaRepository: StageQueryInfoJpaRepository,
) : StageQueryInfoRepository {

    override fun save(stageQueryInfo: StageQueryInfo): StageQueryInfo {
        return stageQueryInfoJpaRepository.save(stageQueryInfo)
    }

    override fun findByStageId(stageId: Long): StageQueryInfo? {
        return stageQueryInfoJpaRepository.findByStageId(stageId)
    }

    override fun deleteByStageId(stageId: Long) {
        stageQueryInfoJpaRepository.deleteByStageId(stageId)
    }
}
