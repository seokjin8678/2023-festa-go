package com.festago.common.aop

import org.slf4j.event.Level

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogRequestBody(
    val exceptionOnly: Boolean = false,
    val level: Level = Level.INFO,
)
