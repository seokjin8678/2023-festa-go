package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.Stage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface StageJpaRepository : JpaRepository<Stage, Long> {

    fun existsByFestivalId(festivalId: Long): Boolean

    fun findAllByFestivalId(festivalId: Long): List<Stage>

    @Query("select s from Stage s join fetch s.festival left join fetch s.artists where s.id = :id")
    fun findByIdWithFetch(@Param("id") id: Long): Stage?
}
