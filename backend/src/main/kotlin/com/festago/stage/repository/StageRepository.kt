package com.festago.stage.repository

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.stage.domain.Stage
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

fun StageRepository.getOrThrow(stageId: Long): Stage {
    return findById(stageId) ?: throw NotFoundException(ErrorCode.STAGE_NOT_FOUND)
}

interface StageRepository : Repository<Stage, Long> {

    fun save(stage: Stage): Stage

    fun findById(stageId: Long): Stage?

    fun deleteById(stageId: Long)

    fun existsByFestivalId(festivalId: Long): Boolean

    fun findAllByFestivalId(festivalId: Long): List<Stage>

    @Query("select s from Stage s join fetch s.festival left join fetch s.artists where s.id = :id")
    fun findByIdWithFetch(@Param("id") id: Long): Stage?
}
