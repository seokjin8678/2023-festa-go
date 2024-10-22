package com.festago.member.repository

import com.festago.auth.domain.SocialType
import com.festago.member.domain.Member
import com.festago.support.AbstractMemoryRepositoryKt

class MemoryMemberRepository : AbstractMemoryRepositoryKt<Member>(), MemberRepository {

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
