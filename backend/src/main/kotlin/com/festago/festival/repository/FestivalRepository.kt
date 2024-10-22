package com.festago.festival.repository

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.domain.Festival
import org.springframework.data.repository.Repository

fun FestivalRepository.getOrThrow(festivalId: Long): Festival {
    return findById(festivalId) ?: throw NotFoundException(ErrorCode.FESTIVAL_NOT_FOUND)
}

interface FestivalRepository : Repository<Festival, Long> {

    fun existsBySchoolId(schoolId: Long): Boolean

    fun save(festival: Festival): Festival

    fun findById(festivalId: Long): Festival?

    fun deleteById(festivalId: Long)

    fun existsById(festivalId: Long): Boolean
}
