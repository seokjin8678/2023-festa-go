package com.festago.admin.application

import com.festago.admin.dto.socialmedia.AdminSocialMediaV1Response
import com.festago.admin.infrastructure.repository.query.AdminSocialMediaV1QueryDslRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.socialmedia.domain.OwnerType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminSocialMediaV1QueryService(
    private val adminSocialMediaV1QueryDslRepository: AdminSocialMediaV1QueryDslRepository,
) {
    fun findById(socialMediaId: Long): AdminSocialMediaV1Response {
        return adminSocialMediaV1QueryDslRepository.findById(socialMediaId)
            ?: throw NotFoundException(ErrorCode.SOCIAL_MEDIA_NOT_FOUND)
    }

    fun findByOwnerIdAndOwnerType(ownerId: Long, ownerType: OwnerType): List<AdminSocialMediaV1Response> {
        return adminSocialMediaV1QueryDslRepository.findByOwnerIdAndOwnerType(ownerId, ownerType)
    }
}
