package com.festago.member.infrastructure.repository

import com.festago.auth.domain.SocialType
import com.festago.member.domain.Member
import com.festago.member.domain.MemberRepository
import com.festago.support.AbstractMemoryRepository

class MemoryMemberRepository : AbstractMemoryRepository<Member>(), MemberRepository {

    override fun delete(member: Member) {
        memory.remove(member.id)
    }

    override fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): Member? {
        return memory.values.asSequence()
            .filter { it.socialId == socialId }
            .filter { it.socialType == socialType }
            .firstOrNull()
    }
}
