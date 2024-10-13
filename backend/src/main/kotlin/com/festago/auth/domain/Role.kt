package com.festago.auth.domain

import com.festago.auth.annotation.Admin
import com.festago.auth.annotation.Anonymous
import com.festago.auth.annotation.Member
import kotlin.reflect.KClass

enum class Role(
    val annotation: KClass<out Annotation>,
) {
    ANONYMOUS(Anonymous::class),
    MEMBER(Member::class),
    ADMIN(Admin::class),
}
