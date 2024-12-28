package com.festago.stage.domain

interface StageQueryInfoRepository {

    fun save(stageQueryInfo: StageQueryInfo): StageQueryInfo

    fun findByStageId(stageId: Long): StageQueryInfo?

    fun deleteByStageId(stageId: Long)
}
