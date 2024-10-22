package com.festago.stage.repository

import com.festago.stage.domain.StageQueryInfo
import org.springframework.data.repository.Repository

interface StageQueryInfoRepository : Repository<StageQueryInfo, Long> {

    fun save(stageQueryInfo: StageQueryInfo): StageQueryInfo

    fun findByStageId(stageId: Long): StageQueryInfo?

    fun deleteByStageId(stageId: Long)
}
