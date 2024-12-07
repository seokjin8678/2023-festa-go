package com.festago.common.filter.wrapping

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
    private val uriPatternMatcher: UriPatternMatcher,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        for ((requestMappingInfo, handlerMethod) in requestMappingHandlerMapping.handlerMethods) {
            val methods = requestMappingInfo.methodsCondition.methods
            if (methods.isNotEmpty()) {
                uriPatternMatcher.addPattern(methods, requestMappingInfo.patternValues)
            }
        }
    }
}
