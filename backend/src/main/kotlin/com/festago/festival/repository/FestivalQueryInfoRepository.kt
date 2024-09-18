package com.festago.festival.repository

import com.festago.festival.domain.FestivalQueryInfo
import org.springframework.data.repository.Repository

interface FestivalQueryInfoRepository : Repository<FestivalQueryInfo, Long> {

    fun save(festivalQueryInfo: FestivalQueryInfo): FestivalQueryInfo

    fun findByFestivalId(festivalId: Long): FestivalQueryInfo?

    fun deleteByFestivalId(festivalId: Long)
}

