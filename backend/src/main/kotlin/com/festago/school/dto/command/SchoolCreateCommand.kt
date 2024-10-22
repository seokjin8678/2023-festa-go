package com.festago.school.dto.command

import com.festago.school.domain.School
import com.festago.school.domain.SchoolRegion

data class SchoolCreateCommand(
    val name: String,
    val domain: String,
    val region: SchoolRegion,
    val logoUrl: String? = null,
    val backgroundImageUrl: String? = null,
) {
    fun toEntity(): School {
        return School(
            id = null,
            domain = domain,
            name = name,
            logoUrl = logoUrl,
            backgroundImageUrl = backgroundImageUrl,
            region = region
        )
    }
}
