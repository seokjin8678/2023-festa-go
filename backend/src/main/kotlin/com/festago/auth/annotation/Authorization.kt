package com.festago.auth.annotation

import com.festago.auth.domain.Role

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authorization(val role: Role)
