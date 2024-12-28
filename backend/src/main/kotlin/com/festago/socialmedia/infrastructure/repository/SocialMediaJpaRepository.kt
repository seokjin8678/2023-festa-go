package com.festago.socialmedia.infrastructure.repository

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMedia
import com.festago.socialmedia.domain.SocialMediaType
import org.springframework.data.jpa.repository.JpaRepository

internal interface SocialMediaJpaRepository : JpaRepository<SocialMedia, Long> {

    fun existsByOwnerIdAndOwnerTypeAndMediaType(
        ownerId: Long,
        ownerType: OwnerType,
        mediaType: SocialMediaType,
    ): Boolean
}
