package com.festago.logging.infrastructure

import com.festago.common.annotation.LoggingDetail
import com.festago.logging.domain.RequestLoggingPolicy
import com.festago.logging.domain.RequestLoggingUriPatternMatcher
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * ApplicationReadyEvent를 통해 Lazy하게 UriPatternMatcher의 패턴을 추가하는 클래스 <br></br>
 */
@Component
class UriPatternInitializer(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val uriPatternMatcher: RequestLoggingUriPatternMatcher,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        for ((requestMappingInfo, handlerMethod) in requestMappingHandlerMapping.handlerMethods) {
            val loggingPolicy = createLoggingPolicy(handlerMethod.getMethodAnnotation(LoggingDetail::class.java))
            if (loggingPolicy.disable) {
                continue
            }
            for (method in requestMappingInfo.methodsCondition.methods) {
                for (patternValue in requestMappingInfo.patternValues) {
                    uriPatternMatcher.addPattern(method.name, patternValue, loggingPolicy)
                }
            }
        }
    }

    private fun createLoggingPolicy(loggingDetail: LoggingDetail?): RequestLoggingPolicy {
        if (loggingDetail == null) {
            return RequestLoggingPolicy.DEFAULT
        }
        return RequestLoggingPolicy(
            disable = loggingDetail.disable,
            hideRequestBody = loggingDetail.hideRequestBody,
            hideResponseBody = loggingDetail.hideResponseBody
        )
    }
}
