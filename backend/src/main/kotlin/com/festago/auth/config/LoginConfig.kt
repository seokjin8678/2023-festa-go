package com.festago.auth.config

import com.festago.auth.annotation.Authorization
import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.auth.infrastructure.token.http.CompositeHttpRequestTokenExtractor
import com.festago.auth.infrastructure.token.http.CookieHttpRequestTokenExtractor
import com.festago.auth.infrastructure.token.http.HeaderHttpRequestTokenExtractor
import com.festago.auth.web.argument.AdminAuthenticationArgumentResolver
import com.festago.auth.web.argument.MemberAuthenticationArgumentResolver
import com.festago.auth.web.argument.RoleArgumentResolver
import com.festago.auth.web.interceptor.AnnotationAuthorizationInterceptor
import com.festago.auth.web.interceptor.FixedAuthorizationInterceptor
import com.festago.common.interceptor.AnnotationDelegateInterceptor
import com.festago.common.interceptor.HttpMethodDelegateInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
private class LoginConfig(
    private val memberAuthenticationTokenExtractor: AuthenticationTokenExtractor,
    private val adminAuthenticationTokenExtractor: AuthenticationTokenExtractor,
    private val compositeAuthenticationTokenExtractor: AuthenticationTokenExtractor,
    private val authenticateContext: AuthenticateContext,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(RoleArgumentResolver(Role.MEMBER, authenticateContext))
        resolvers.add(RoleArgumentResolver(Role.ADMIN, authenticateContext))
        resolvers.add(MemberAuthenticationArgumentResolver(authenticateContext))
        resolvers.add(AdminAuthenticationArgumentResolver(authenticateContext))
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
            HttpMethodDelegateInterceptor.builder()
                .allowMethod(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH)
                .interceptor(adminFixedAuthorizationInterceptor())
                .build()
        )
            .addPathPatterns("/admin/**")
            .excludePathPatterns("/admin/api/v1/auth/login", "/admin/api/v1/auth/initialize")

        registry.addInterceptor(
            HttpMethodDelegateInterceptor.builder()
                .allowMethod(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH)
                .interceptor(memberFixedAuthorizationInterceptor())
                .build()
        )
            .addPathPatterns("/member-tickets/**", "/members/**", "/auth/**", "/students/**", "/member-fcm/**")
            .excludePathPatterns("/auth/oauth2")

        registry.addInterceptor(
            AnnotationDelegateInterceptor.builder()
                .annotation(Authorization::class.java)
                .interceptor(annotationAuthorizationInterceptor())
                .build()
        )
            .addPathPatterns("/api/**")
    }

    @Bean
    fun adminFixedAuthorizationInterceptor(): FixedAuthorizationInterceptor {
        return FixedAuthorizationInterceptor(
            compositeHttpRequestTokenExtractor(),
            adminAuthenticationTokenExtractor,
            authenticateContext,
            Role.ADMIN
        )
    }

    @Bean
    fun memberFixedAuthorizationInterceptor(): FixedAuthorizationInterceptor {
        return FixedAuthorizationInterceptor(
            compositeHttpRequestTokenExtractor(),
            memberAuthenticationTokenExtractor,
            authenticateContext,
            Role.MEMBER
        )
    }

    @Bean
    fun annotationAuthorizationInterceptor(): AnnotationAuthorizationInterceptor {
        return AnnotationAuthorizationInterceptor(
            compositeHttpRequestTokenExtractor(),
            compositeAuthenticationTokenExtractor,
            authenticateContext
        )
    }

    @Bean
    fun compositeHttpRequestTokenExtractor(
    ): CompositeHttpRequestTokenExtractor {
        return CompositeHttpRequestTokenExtractor(
            listOf(
                HeaderHttpRequestTokenExtractor(),
                CookieHttpRequestTokenExtractor()
            )
        )
    }
}
