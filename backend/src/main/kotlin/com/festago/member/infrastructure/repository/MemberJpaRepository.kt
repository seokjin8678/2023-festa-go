package com.festago.member.infrastructure.repository

import com.festago.auth.domain.SocialType
import com.festago.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

internal interface MemberJpaRepository : JpaRepository<Member, Long> {

    fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): Member?
}
