package com.festago.member.infrastructure.repository

import com.festago.auth.domain.SocialType
import com.festago.member.domain.Member
import com.festago.member.domain.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class MemberRepositoryImpl(
    private val memberJpaRepository: MemberJpaRepository,
) : MemberRepository {

    override fun save(member: Member): Member {
        return memberJpaRepository.save(member)
    }

    override fun findById(id: Long): Member? {
        return memberJpaRepository.findByIdOrNull(id)
    }

    override fun delete(member: Member) {
        return memberJpaRepository.delete(member)
    }

    override fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): Member? {
        return memberJpaRepository.findBySocialIdAndSocialType(socialId, socialType)
    }
}
