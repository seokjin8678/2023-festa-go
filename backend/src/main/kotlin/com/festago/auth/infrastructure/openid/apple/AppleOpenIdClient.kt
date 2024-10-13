package com.festago.auth.infrastructure.openid.apple

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.domain.openid.OpenIdClient
import com.festago.auth.domain.openid.OpenIdNonceValidator
import com.festago.auth.domain.token.jwt.OpenIdIdTokenParser
import com.festago.auth.infrastructure.openid.CachedOpenIdKeyProvider
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Locator
import java.security.Key
import java.time.Clock
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
internal class AppleOpenIdClient(
    private val openIdNonceValidator: OpenIdNonceValidator,
    @Value("\${festago.oauth2.apple.client-id}") clientId: String,
    appleOpenIdPublicKeyLocator: AppleOpenIdPublicKeyLocator,
    clock: Clock,
) : OpenIdClient {

    private val idTokenParser = OpenIdIdTokenParser(Jwts.parser()
        .keyLocator(appleOpenIdPublicKeyLocator)
        .requireAudience(clientId)
        .requireIssuer(ISSUER)
        .clock { Date.from(clock.instant()) }
        .build())

    override fun getUserInfo(idToken: String): UserInfo {
        val payload = idTokenParser.parse(idToken)
        openIdNonceValidator.validate(payload.get("nonce", String::class.java), payload.expiration)
        return UserInfo(
            socialType = SocialType.APPLE,
            socialId = payload.subject
        )
    }

    override val socialType: SocialType = SocialType.APPLE

    companion object {
        private const val ISSUER = "https://appleid.apple.com"
    }
}

@Component
internal class AppleOpenIdPublicKeyLocator(
    private val appleOpenIdJwksClient: AppleOpenIdJwksClient,
    private val cachedOpenIdKeyProvider: CachedOpenIdKeyProvider,
) : Locator<Key> {

    override fun locate(header: Header): Key? {
        val kid = header["kid"] as? String?
            ?: throw UnauthorizedException(ErrorCode.OPEN_ID_INVALID_TOKEN)
        return cachedOpenIdKeyProvider.provide(kid) { appleOpenIdJwksClient.requestGetJwks() }
    }
}
