package com.festago.festival.infrastructure.repository

import com.festago.festival.domain.FestivalQueryInfo
import com.festago.festival.domain.FestivalQueryInfoRepository
import org.springframework.stereotype.Repository

@Repository
class FestivalQueryInfoRepositoryImpl(
    private val festivalQueryInfoJpaRepository: FestivalQueryInfoJpaRepository,
) : FestivalQueryInfoRepository {

    override fun save(festivalQueryInfo: FestivalQueryInfo): FestivalQueryInfo {
        return festivalQueryInfoJpaRepository.save(festivalQueryInfo)
    }

    override fun findByFestivalId(festivalId: Long): FestivalQueryInfo? {
        return festivalQueryInfoJpaRepository.findByFestivalId(festivalId)
    }

    override fun deleteByFestivalId(festivalId: Long) {
        festivalQueryInfoJpaRepository.deleteByFestivalId(festivalId)
    }
}
