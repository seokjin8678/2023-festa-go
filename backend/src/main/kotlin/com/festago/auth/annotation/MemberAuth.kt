package com.festago.auth.annotation

import com.festago.auth.domain.Role
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@SecurityRequirement(name = "bearerAuth")
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@Authorization(role = Role.MEMBER)
annotation class MemberAuth 
