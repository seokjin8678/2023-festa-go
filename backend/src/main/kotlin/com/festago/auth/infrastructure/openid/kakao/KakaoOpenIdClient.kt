package com.festago.auth.infrastructure.openid.kakao

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.domain.openid.OpenIdClient
import com.festago.auth.domain.openid.OpenIdNonceValidator
import com.festago.auth.domain.token.jwt.OpenIdIdTokenParser
import com.festago.auth.infrastructure.openid.CachedOpenIdKeyProvider
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Locator
import java.security.Key
import java.time.Clock
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
internal class KakaoOpenIdClient(
    private val openIdNonceValidator: OpenIdNonceValidator,
    @Value("\${festago.oauth2.kakao.rest-api-key}") restApiKey: String,
    @Value("\${festago.oauth2.kakao.native-app-key}") nativeAppKey: String,
    kakaoOpenIdPublicKeyLocator: KakaoOpenIdPublicKeyLocator,
    clock: Clock,
) : OpenIdClient {

    private val idTokenParser = OpenIdIdTokenParser(Jwts.parser()
        .keyLocator(kakaoOpenIdPublicKeyLocator)
        .requireIssuer(ISSUER)
        .clock { Date.from(clock.instant()) }
        .build())
    private val appKeys = setOf(restApiKey, nativeAppKey)

    override fun getUserInfo(idToken: String): UserInfo {
        val payload = idTokenParser.parse(idToken)
        openIdNonceValidator.validate(payload.get("nonce", String::class.java), payload.expiration)
        validateAudience(payload.audience)
        return UserInfo(
            socialType = SocialType.KAKAO,
            socialId = payload.subject
        )
    }

    private fun validateAudience(audiences: Set<String>) {
        for (audience in audiences) {
            if (appKeys.contains(audience)) {
                return
            }
        }
        log.info { "허용되지 않는 id 토큰의 audience 값이 요청되었습니다. audiences=${audiences}" }
        throw UnauthorizedException(ErrorCode.OPEN_ID_INVALID_TOKEN)
    }

    override val socialType = SocialType.KAKAO

    companion object {
        private const val ISSUER = "https://kauth.kakao.com"
    }
}

@Component
internal class KakaoOpenIdPublicKeyLocator(
    private val kakaoOpenIdJwksClient: KakaoOpenIdJwksClient,
    private val cachedOpenIdKeyProvider: CachedOpenIdKeyProvider,
) : Locator<Key> {

    override fun locate(header: Header): Key? {
        val kid = header["kid"] as? String?
            ?: throw UnauthorizedException(ErrorCode.OPEN_ID_INVALID_TOKEN)
        return cachedOpenIdKeyProvider.provide(kid) { kakaoOpenIdJwksClient.requestGetJwks() }
    }
}