package com.festago.auth.dto.event

import com.festago.member.domain.Member

data class MemberCreatedEvent(
    val member: Member,
)
