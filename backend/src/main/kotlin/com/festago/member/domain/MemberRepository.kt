package com.festago.member.domain

import com.festago.auth.domain.SocialType
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException

fun MemberRepository.getOrThrow(id: Long): Member {
    return findById(id) ?: throw NotFoundException(ErrorCode.MEMBER_NOT_FOUND)
}

interface MemberRepository {

    fun save(member: Member): Member

    fun findById(id: Long): Member?

    fun delete(member: Member)

    fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): Member?
}
