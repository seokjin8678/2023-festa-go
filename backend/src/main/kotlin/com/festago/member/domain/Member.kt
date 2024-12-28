package com.festago.member.domain

import com.festago.auth.domain.SocialType
import com.festago.common.domain.BaseTimeEntity
import com.festago.common.util.Validator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@SQLDelete(sql = "UPDATE member SET deleted_at = now(), nickname = '탈퇴한 회원', profile_image_url = '', social_id = null WHERE id = ?")
@SQLRestriction("deleted_at is null")
class Member(
    id: Long? = null,
    socialId: String,
    socialType: SocialType,
    nickname: String,
    profileImage: String = "",
) : BaseTimeEntity() {

    constructor(
        socialId: String,
        socialType: SocialType,
        nickname: String,
    ) : this(id = null, socialId = socialId, socialType = socialType, nickname = nickname)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    var socialId: String? = socialId
        protected set

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar")
    var socialType: SocialType = socialType
        protected set

    var nickname: String = nickname
        protected set

    @Column(name = "profile_image_url")
    var profileImage: String = profileImage
        protected set

    var deletedAt: LocalDateTime? = null
        protected set

    init {
        validateSocialId(socialId)
        validateNickname(nickname)
        validateProfileImage(profileImage)
    }

    private fun validateSocialId(socialId: String) {
        Validator.notBlank(socialId, "socialId")
    }

    private fun validateNickname(nickname: String) {
        val fieldName = "nickname"
        Validator.notBlank(nickname, fieldName)
        Validator.maxLength(nickname, MAX_NICKNAME_LENGTH, fieldName)
    }

    private fun validateProfileImage(profileImage: String?) {
        Validator.maxLength(profileImage, MAX_PROFILE_IMAGE_LENGTH, "profileImage")
    }

    val identifier: Long
        get() = id!!

    val isDeleted: Boolean
        get() = deletedAt != null

    companion object {
        private const val MAX_NICKNAME_LENGTH = 30
        private const val MAX_PROFILE_IMAGE_LENGTH = 255
    }
}
