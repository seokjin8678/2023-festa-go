package com.festago.stage.repository

import com.festago.stage.domain.StageQueryInfo
import com.festago.support.AbstractMemoryRepository

class MemoryStageQueryInfoRepository : AbstractMemoryRepository<StageQueryInfo>(), StageQueryInfoRepository {

    override fun findByStageId(stageId: Long): StageQueryInfo? {
        return memory.values.firstOrNull { stageQueryInfo: StageQueryInfo -> stageQueryInfo.stageId == stageId }
    }

    override fun deleteByStageId(stageId: Long) {
        memory.entries.removeIf { it.value.stageId == stageId }
    }
}
