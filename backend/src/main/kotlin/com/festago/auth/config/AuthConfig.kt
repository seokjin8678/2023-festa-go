package com.festago.auth.config

import com.festago.auth.domain.oauth2.OAuth2Client
import com.festago.auth.domain.oauth2.OAuth2Clients
import com.festago.auth.domain.openid.OpenIdClient
import com.festago.auth.domain.openid.OpenIdClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
private class AuthConfig {

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
