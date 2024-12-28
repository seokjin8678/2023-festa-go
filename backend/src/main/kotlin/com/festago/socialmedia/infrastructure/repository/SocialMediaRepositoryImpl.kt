package com.festago.socialmedia.infrastructure.repository

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMedia
import com.festago.socialmedia.domain.SocialMediaRepository
import com.festago.socialmedia.domain.SocialMediaType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class SocialMediaRepositoryImpl(
    private val socialMediaJpaRepository: SocialMediaJpaRepository,
) : SocialMediaRepository {

    override fun save(socialMedia: SocialMedia): SocialMedia {
        return socialMediaJpaRepository.save(socialMedia)
    }

    override fun findById(id: Long): SocialMedia? {
        return socialMediaJpaRepository.findByIdOrNull(id)
    }

    override fun existsByOwnerIdAndOwnerTypeAndMediaType(
        ownerId: Long,
        ownerType: OwnerType,
        mediaType: SocialMediaType,
    ): Boolean {
        return socialMediaJpaRepository.existsByOwnerIdAndOwnerTypeAndMediaType(
            ownerId = ownerId,
            ownerType = ownerType,
            mediaType = mediaType
        )
    }

    override fun deleteById(socialMediaId: Long) {
        return socialMediaJpaRepository.deleteById(socialMediaId)
    }
}
