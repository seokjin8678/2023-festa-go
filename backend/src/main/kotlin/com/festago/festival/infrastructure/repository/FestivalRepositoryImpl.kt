package com.festago.festival.infrastructure.repository

import com.festago.festival.domain.Festival
import com.festago.festival.domain.FestivalRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class FestivalRepositoryImpl(
    private val festivalJpaRepository: FestivalJpaRepository,
) : FestivalRepository {

    override fun existsBySchoolId(schoolId: Long): Boolean {
        return festivalJpaRepository.existsBySchoolId(schoolId)
    }

    override fun save(festival: Festival): Festival {
        return festivalJpaRepository.save(festival)
    }

    override fun findById(festivalId: Long): Festival? {
        return festivalJpaRepository.findByIdOrNull(festivalId)
    }

    override fun deleteById(festivalId: Long) {
        return festivalJpaRepository.deleteById(festivalId)
    }

    override fun existsById(festivalId: Long): Boolean {
        return festivalJpaRepository.existsById(festivalId)
    }
}
