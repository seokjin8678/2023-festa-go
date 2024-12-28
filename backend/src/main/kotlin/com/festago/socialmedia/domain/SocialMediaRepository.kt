package com.festago.socialmedia.domain

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException

fun SocialMediaRepository.getOrThrow(id: Long): SocialMedia {
    return findById(id) ?: throw NotFoundException(ErrorCode.SOCIAL_MEDIA_NOT_FOUND)
}

interface SocialMediaRepository {

    fun save(socialMedia: SocialMedia): SocialMedia

    fun findById(id: Long): SocialMedia?

    fun existsByOwnerIdAndOwnerTypeAndMediaType(
        ownerId: Long,
        ownerType: OwnerType,
        mediaType: SocialMediaType,
    ): Boolean

    fun deleteById(socialMediaId: Long)
}
