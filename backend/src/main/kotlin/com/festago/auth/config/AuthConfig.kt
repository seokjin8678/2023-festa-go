package com.festago.auth.config

import com.festago.auth.annotation.Authorization
import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.auth.domain.oauth2.OAuth2Client
import com.festago.auth.domain.oauth2.OAuth2Clients
import com.festago.auth.domain.openid.OpenIdClient
import com.festago.auth.domain.openid.OpenIdClients
import com.festago.auth.domain.token.http.CompositeHttpRequestTokenExtractor
import com.festago.auth.domain.token.http.CookieHttpRequestTokenExtractor
import com.festago.auth.domain.token.http.HeaderHttpRequestTokenExtractor
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.auth.web.argument.AdminAuthenticationArgumentResolver
import com.festago.auth.web.argument.AuthenticationArgumentResolver
import com.festago.auth.web.argument.MemberAuthenticationArgumentResolver
import com.festago.auth.web.filter.AuthenticationSettingFilter
import com.festago.auth.web.interceptor.AnnotationAuthorizationInterceptor
import com.festago.auth.web.interceptor.FixedAuthorizationInterceptor
import com.festago.common.interceptor.AnnotationDelegateInterceptor
import com.festago.common.interceptor.HttpMethodDelegateInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
private class AuthConfig(
    private val authenticateContext: AuthenticateContext,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(MemberAuthenticationArgumentResolver(authenticateContext))
        resolvers.add(AdminAuthenticationArgumentResolver(authenticateContext))
        resolvers.add(AuthenticationArgumentResolver(authenticateContext))
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
            authenticateContext,
            Role.ADMIN
        )
    }

    @Bean
    fun annotationAuthorizationInterceptor(): AnnotationAuthorizationInterceptor {
        return AnnotationAuthorizationInterceptor(
            authenticateContext
        )
    }

    @Bean
    fun authenticationSettingFilter(
        compositeAuthenticationTokenExtractor: AuthenticationTokenExtractor,
    ): AuthenticationSettingFilter {
        return AuthenticationSettingFilter(
            authenticateContext = authenticateContext,
            httpRequestTokenExtractor = compositeHttpRequestTokenExtractor(),
            authenticationTokenExtractor = compositeAuthenticationTokenExtractor
        )
    }

    @Bean
    fun compositeHttpRequestTokenExtractor(): CompositeHttpRequestTokenExtractor {
        return CompositeHttpRequestTokenExtractor(
            listOf(
                HeaderHttpRequestTokenExtractor(),
                CookieHttpRequestTokenExtractor()
            )
        )
    }

    @Bean
    fun oAuth2Clients(oAuth2Clients: List<OAuth2Client>): OAuth2Clients {
        return OAuth2Clients.builder()
            .addAll(oAuth2Clients)
            .build()
    }

    @Bean
    fun openIdClients(openIdClients: List<OpenIdClient>): OpenIdClients {
        return OpenIdClients.builder()
            .addAll(openIdClients)
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}
