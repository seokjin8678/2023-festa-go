package com.festago.common.interceptor

import com.festago.common.exception.UnexpectedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

class AnnotationDelegateInterceptor(
    private val annotation: Class<out Annotation>,
    private val interceptor: HandlerInterceptor,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as HandlerMethod
        if (handlerMethod.hasMethodAnnotation(annotation)) {
            return interceptor.preHandle(request, response, handler)
        }
        return true
    }

    class AnnotationsDelegateInterceptorBuilder {
        private var annotation: Class<out Annotation>? = null
        private var interceptor: HandlerInterceptor? = null

        fun annotation(annotation: Class<out Annotation>): AnnotationsDelegateInterceptorBuilder {
            this.annotation = annotation
            return this
        }

        fun interceptor(interceptor: HandlerInterceptor): AnnotationsDelegateInterceptorBuilder {
            this.interceptor = interceptor
            return this
        }

        fun build(): AnnotationDelegateInterceptor {
            if (annotation == null) {
                throw UnexpectedException("annotation은 null이 될 수 없습니다.")
            }
            if (interceptor == null) {
                throw UnexpectedException("interceptor는 null이 될 수 없습니다.")
            }
            return AnnotationDelegateInterceptor(annotation!!, interceptor!!)
        }
    }

    companion object {
        fun builder(): AnnotationsDelegateInterceptorBuilder {
            return AnnotationsDelegateInterceptorBuilder()
        }
    }
}
