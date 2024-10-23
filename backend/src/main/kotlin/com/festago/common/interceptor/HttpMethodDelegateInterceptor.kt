package com.festago.common.interceptor

import com.festago.common.exception.UnexpectedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.HandlerInterceptor

class HttpMethodDelegateInterceptor(
    private val allowMethods: Set<String>,
    private val interceptor: HandlerInterceptor,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (allowMethods.contains(request.method)) {
            return interceptor.preHandle(request, response, handler)
        }
        return true
    }

    class HttpMethodDelegateInterceptorBuilder {
        private val allowMethod: MutableSet<HttpMethod> = HashSet()
        private var interceptor: HandlerInterceptor? = null

        fun allowMethod(vararg httpMethods: HttpMethod): HttpMethodDelegateInterceptorBuilder {
            allowMethod.addAll(httpMethods)
            return this
        }

        fun interceptor(interceptor: HandlerInterceptor): HttpMethodDelegateInterceptorBuilder {
            this.interceptor = interceptor
            return this
        }

        fun build(): HttpMethodDelegateInterceptor {
            if (interceptor == null) {
                throw UnexpectedException("interceptor는 null이 될 수 없습니다.")
            }
            val methods = allowMethod.map { it.name() }.toSet()
            return HttpMethodDelegateInterceptor(
                methods,
                interceptor!!
            )
        }
    }

    companion object {
        fun builder(): HttpMethodDelegateInterceptorBuilder {
            return HttpMethodDelegateInterceptorBuilder()
        }
    }
}
