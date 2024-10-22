package com.festago.socialmedia.repository

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMedia
import com.festago.socialmedia.domain.SocialMediaType
import org.springframework.data.repository.Repository

fun SocialMediaRepository.getOrThrow(id: Long): SocialMedia {
    return findById(id) ?: throw NotFoundException(ErrorCode.SOCIAL_MEDIA_NOT_FOUND)
}

interface SocialMediaRepository : Repository<SocialMedia, Long> {

    fun save(socialMedia: SocialMedia): SocialMedia

    fun findById(id: Long): SocialMedia?

    fun existsByOwnerIdAndOwnerTypeAndMediaType(
        ownerId: Long,
        ownerType: OwnerType,
        mediaType: SocialMediaType,
    ): Boolean

    fun deleteById(socialMediaId: Long)
}
