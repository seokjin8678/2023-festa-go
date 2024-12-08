package com.festago.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.TaskDecorator
import org.springframework.core.task.TaskExecutor
import org.springframework.core.task.support.CompositeTaskDecorator
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.context.request.RequestContextHolder

private val log = KotlinLogging.logger {}

@EnableAsync
@Configuration
class AsyncConfig {

    @Bean
    @Primary
    fun taskExecutor(): TaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            setThreadNamePrefix("asyncExecutor-")
            setTaskDecorator(
                CompositeTaskDecorator(
                    listOf(
                        LogTaskDecorator,
                        RequestContextTaskDecorator
                    )
                )
            )
            initialize()
        }
    }
}

private object LogTaskDecorator : TaskDecorator {

    override fun decorate(runnable: Runnable): Runnable {
        return Runnable {
            try {
                runnable.run()
            } catch (e: Exception) {
                log.error(e) { e.message }
            }
        }
    }
}

private object RequestContextTaskDecorator : TaskDecorator {

    override fun decorate(runnable: Runnable): Runnable {
        val context = RequestContextHolder.currentRequestAttributes()
        return Runnable {
            try {
                RequestContextHolder.setRequestAttributes(context)
                runnable.run()
            } finally {
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }
}
