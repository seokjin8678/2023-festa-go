package com.festago.admin.dto.school

import com.festago.school.domain.SchoolRegion
import com.festago.school.dto.command.SchoolCreateCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SchoolV1CreateRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val domain: String,
    @field:NotNull
    val region: SchoolRegion,
    val logoUrl: String?,
    val backgroundImageUrl: String?,
) {

    fun toCommand(): SchoolCreateCommand {
        return SchoolCreateCommand(name, domain, region, logoUrl, backgroundImageUrl)
    }
}
