package com.festago.admin.infrastructure.repository.query

import com.festago.admin.dto.socialmedia.AdminSocialMediaV1Response
import com.festago.admin.dto.socialmedia.QAdminSocialMediaV1Response
import com.festago.common.querydsl.QueryDslHelper
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.QSocialMedia.socialMedia
import org.springframework.stereotype.Repository

@Repository
class AdminSocialMediaV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findById(socialMediaId: Long): AdminSocialMediaV1Response? {
        return queryDslHelper.fetchOne {
            it.select(
                QAdminSocialMediaV1Response(
                    socialMedia.id,
                    socialMedia.ownerId,
                    socialMedia.ownerType,
                    socialMedia.mediaType,
                    socialMedia.name,
                    socialMedia.logoUrl,
                    socialMedia.url
                )
            )
                .from(socialMedia)
                .where(socialMedia.id.eq(socialMediaId))
        }
    }

    // SocialMedia의 도메인 특성 상 SocialMediaType 개수만큼 row 생성이 제한되기에 limit을 사용하지 않음
    fun findByOwnerIdAndOwnerType(ownerId: Long, ownerType: OwnerType): List<AdminSocialMediaV1Response> {
        return queryDslHelper.select(
            QAdminSocialMediaV1Response(
                socialMedia.id,
                socialMedia.ownerId,
                socialMedia.ownerType,
                socialMedia.mediaType,
                socialMedia.name,
                socialMedia.logoUrl,
                socialMedia.url
            )
        )
            .from(socialMedia)
            .where(socialMedia.ownerId.eq(ownerId).and(socialMedia.ownerType.eq(ownerType)))
            .fetch()
    }
}
