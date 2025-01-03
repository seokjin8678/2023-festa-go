package com.festago.auth.domain

import com.festago.auth.domain.model.UserInfo
import com.festago.member.domain.DefaultNicknamePolicy
import com.festago.member.domain.Member
import org.springframework.stereotype.Component

@Component
class UserInfoMemberMapper(
    private val defaultNicknamePolicy: DefaultNicknamePolicy,
) {

    fun toMember(userInfo: UserInfo): Member {
        val nickname: String? = userInfo.nickname
        return Member(
            socialId = userInfo.socialId,
            socialType = userInfo.socialType,
            nickname = if (nickname.isNullOrBlank()) defaultNicknamePolicy.generate() else nickname,
            profileImage = userInfo.profileImage ?: ""
        )
    }
}
