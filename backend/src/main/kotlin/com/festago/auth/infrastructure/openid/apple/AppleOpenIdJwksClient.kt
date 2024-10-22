package com.festago.auth.infrastructure.openid.apple

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
internal class AppleOpenIdJwksClient(
    restTemplateBuilder: RestTemplateBuilder,
) {
    private val restClient = RestClient.create(
        restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(3))
            .build()
    )
    private val parser: Parser<JwkSet> = Jwks.setParser().build()

    fun requestGetJwks(): JwkSet {
        val jsonKeys = restClient.get()
            .uri(URI)
            .retrieve()
            .onStatus(AppleOpenIdJwksClientErrorHandler)
            .body<String>()
        log.info { "Apple JWKS 공개키 목록을 조회했습니다." }
        return parser.parse(jsonKeys)
    }

    companion object {
        private const val URI = "https://appleid.apple.com/auth/keys"
    }
}
