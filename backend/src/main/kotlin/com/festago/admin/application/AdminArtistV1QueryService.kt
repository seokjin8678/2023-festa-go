package com.festago.admin.application

import com.festago.admin.dto.artist.AdminArtistV1Response
import com.festago.admin.repository.AdminArtistV1QueryDslRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.querydsl.SearchCondition
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminArtistV1QueryService(
    private val adminArtistV1QueryDslRepository: AdminArtistV1QueryDslRepository,
) {

    fun findById(artistId: Long): AdminArtistV1Response {
        return adminArtistV1QueryDslRepository.findById(artistId)
            ?: throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
    }

    fun findAll(searchCondition: SearchCondition): Page<AdminArtistV1Response> {
        return adminArtistV1QueryDslRepository.findAll(searchCondition)
    }
}
