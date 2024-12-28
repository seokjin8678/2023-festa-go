package com.festago.admin.application

import com.festago.admin.dto.festival.AdminFestivalDetailV1Response
import com.festago.admin.dto.festival.AdminFestivalV1Response
import com.festago.admin.infrastructure.repository.query.AdminFestivalDetailV1QueryDslRepository
import com.festago.admin.infrastructure.repository.query.AdminFestivalV1QueryDslRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.querydsl.SearchCondition
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFestivalV1QueryService(
    private val adminFestivalV1QueryDslRepository: AdminFestivalV1QueryDslRepository,
    private val adminFestivalDetailV1QueryDslRepository: AdminFestivalDetailV1QueryDslRepository,
) {

    fun findAll(searchCondition: SearchCondition): Page<AdminFestivalV1Response> {
        return adminFestivalV1QueryDslRepository.findAll(searchCondition)
    }

    fun findDetail(festivalId: Long): AdminFestivalDetailV1Response {
        return adminFestivalDetailV1QueryDslRepository.findDetail(festivalId)
            ?: throw NotFoundException(ErrorCode.FESTIVAL_NOT_FOUND)
    }
}
