package com.festago.auth.dto.event

import com.festago.member.domain.Member

data class MemberDeletedEvent(
    val member: Member,
)
