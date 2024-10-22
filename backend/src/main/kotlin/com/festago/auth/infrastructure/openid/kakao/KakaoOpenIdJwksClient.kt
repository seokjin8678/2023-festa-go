package com.festago.auth.infrastructure.openid.kakao

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.io.Parser
import io.jsonwebtoken.security.JwkSet
import io.jsonwebtoken.security.Jwks
import java.time.Duration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

private val log = KotlinLogging.logger {}

@Component
internal class KakaoOpenIdJwksClient(
    restTemplateBuilder: RestTemplateBuilder,
) {
    private val restClient = RestClient.create(
        restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(3))
            .build()
    )
    private val parser: Parser<JwkSet> = Jwks.setParser().build()

    // 너무 많은 요청이 오면 차단될 수 있음
    fun requestGetJwks(): JwkSet {
        val jsonKeys = restClient.get()
            .uri("https://kauth.kakao.com/.well-known/jwks.json")
            .retrieve()
            .onStatus(KakaoOpenIdJwksClientErrorHandler)
            .body<String>()
        log.info { "카카오 JWKS 공개키 목록을 조회했습니다." }
        return parser.parse(jsonKeys)
    }
}
