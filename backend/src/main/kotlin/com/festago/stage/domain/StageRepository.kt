package com.festago.stage.domain

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException

fun StageRepository.getOrThrow(stageId: Long): Stage {
    return findById(stageId) ?: throw NotFoundException(ErrorCode.STAGE_NOT_FOUND)
}

interface StageRepository {

    fun save(stage: Stage): Stage

    fun findById(stageId: Long): Stage?

    fun deleteById(stageId: Long)

    fun existsByFestivalId(festivalId: Long): Boolean

    fun findAllByFestivalId(festivalId: Long): List<Stage>

    fun findByIdWithFetch(id: Long): Stage?
}
