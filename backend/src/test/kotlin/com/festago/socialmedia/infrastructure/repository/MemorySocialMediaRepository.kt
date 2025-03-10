package com.festago.socialmedia.infrastructure.repository

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMedia
import com.festago.socialmedia.domain.SocialMediaRepository
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.support.AbstractMemoryRepository

class MemorySocialMediaRepository : AbstractMemoryRepository<SocialMedia>(), SocialMediaRepository {

    override fun existsByOwnerIdAndOwnerTypeAndMediaType(
        ownerId: Long,
        ownerType: OwnerType,
        mediaType: SocialMediaType,
    ): Boolean {
        return memory.values.asSequence()
            .filter { it.ownerId == ownerId }
            .filter { it.ownerType == ownerType }
            .filter { it.mediaType == mediaType }
            .any()
    }
}
