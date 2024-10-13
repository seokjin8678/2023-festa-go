package com.festago.auth.infrastructure.oauth2.kakao

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.festago.common.exception.UnexpectedException
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
internal class KakaoOAuth2TokenClient(
    @Value("\${festago.oauth2.kakao.grant-type}")
    private val grantType: String,
    @Value("\${festago.oauth2.kakao.rest-api-key}")
    private val clientId: String,
    @Value("\${festago.oauth2.kakao.redirect-uri}")
    private val redirectUri: String,
    @Value("\${festago.oauth2.kakao.client-secret}")
    private val clientSecret: String,
    restTemplateBuilder: RestTemplateBuilder,
) {
    private val restClient = RestClient.create(
        restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(3))
            .build()
    )

    fun getIdToken(code: String): String {
        val response = restClient.post()
            .uri(URI)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply {
                add("grant_type", grantType)
                add("client_id", clientId)
                add("redirect_uri", redirectUri)
                add("client_secret", clientSecret)
                add("code", code)
            })
            .retrieve()
            .onStatus(KakaoOAuth2TokenClientErrorHandler)
            .body<KakaoOAuth2TokenResponse>()
        return response?.idToken
            ?: throw UnexpectedException("카카오 OAuth2 토큰 응답에 idToken 필드가 존재하지 않습니다.")
    }

    companion object {
        private const val URI = "https://kauth.kakao.com/oauth/token"
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
private data class KakaoOAuth2TokenResponse(
    val accessToken: String?,
    val idToken: String?,
)
