package com.festago.common.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidPageable(
    val maxSize: Int = 20,
    val sizeKey: String = "size",
)
