package com.festago.socialmedia.repository

import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMedia
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.support.AbstractMemoryRepositoryKt

class MemorySocialMediaRepository : AbstractMemoryRepositoryKt<SocialMedia>(), SocialMediaRepository {

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
