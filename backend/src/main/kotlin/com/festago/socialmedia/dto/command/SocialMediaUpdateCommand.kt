package com.festago.socialmedia.dto.command

data class SocialMediaUpdateCommand(
    val name: String,
    val url: String,
    val logoUrl: String?,
)
