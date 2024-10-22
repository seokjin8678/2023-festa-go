package com.festago.school.dto.command

import com.festago.school.domain.SchoolRegion

data class SchoolUpdateCommand(
    val name: String,
    val domain: String,
    val region: SchoolRegion,
    val logoUrl: String? = null,
    val backgroundImageUrl: String? = null,
)
