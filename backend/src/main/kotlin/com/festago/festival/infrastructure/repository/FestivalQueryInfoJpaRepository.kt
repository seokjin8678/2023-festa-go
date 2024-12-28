package com.festago.festival.infrastructure.repository

import com.festago.festival.domain.FestivalQueryInfo
import org.springframework.data.jpa.repository.JpaRepository

interface FestivalQueryInfoJpaRepository : JpaRepository<FestivalQueryInfo, Long> {

    fun findByFestivalId(festivalId: Long): FestivalQueryInfo?

    fun deleteByFestivalId(festivalId: Long)
}
