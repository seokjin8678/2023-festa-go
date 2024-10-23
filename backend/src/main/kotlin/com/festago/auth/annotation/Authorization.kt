package com.festago.auth.annotation

import com.festago.auth.domain.Role

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authorization(val allowRoles: Array<Role>)
