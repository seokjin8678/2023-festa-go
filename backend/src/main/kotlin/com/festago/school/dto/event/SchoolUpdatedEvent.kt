package com.festago.school.dto.event

import com.festago.school.domain.School

data class SchoolUpdatedEvent(
    val school: School,
)
