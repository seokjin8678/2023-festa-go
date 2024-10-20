package com.festago.school.dto.event

import com.festago.school.domain.School

data class SchoolCreatedEvent(
    val school: School,
)
