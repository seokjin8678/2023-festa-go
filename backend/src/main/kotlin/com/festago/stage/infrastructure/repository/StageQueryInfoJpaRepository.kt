package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.StageQueryInfo
import org.springframework.data.jpa.repository.JpaRepository

internal interface StageQueryInfoJpaRepository : JpaRepository<StageQueryInfo, Long> {

    fun findByStageId(stageId: Long): StageQueryInfo?

    fun deleteByStageId(stageId: Long)
}
