package com.festago.admin.application

import com.festago.admin.dto.stage.AdminStageV1Response
import com.festago.admin.repository.AdminStageV1QueryDslRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminStageV1QueryService(
    private val adminStageV1QueryDslRepository: AdminStageV1QueryDslRepository,
) {

    fun findAllByFestivalId(festivalId: Long): List<AdminStageV1Response> {
        return adminStageV1QueryDslRepository.findAllByFestivalId(festivalId)
    }

    fun findById(stageId: Long): AdminStageV1Response {
        return adminStageV1QueryDslRepository.findById(stageId)
            ?: throw NotFoundException(ErrorCode.STAGE_NOT_FOUND)
    }
}
