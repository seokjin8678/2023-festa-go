package com.festago.festival.domain

interface FestivalQueryInfoRepository {

    fun save(festivalQueryInfo: FestivalQueryInfo): FestivalQueryInfo

    fun findByFestivalId(festivalId: Long): FestivalQueryInfo?

    fun deleteByFestivalId(festivalId: Long)
}

