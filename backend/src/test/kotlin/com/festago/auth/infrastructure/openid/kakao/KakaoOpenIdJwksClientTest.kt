package com.festago.auth.infrastructure.openid.kakao

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(KakaoOpenIdJwksClient::class)
internal class KakaoOpenIdJwksClientTest {

    val url = "https://kauth.kakao.com/.well-known/jwks.json"

    @Autowired
    lateinit var kakaoOpenIdJwksClient: KakaoOpenIdJwksClient

    @Autowired
    lateinit var mockServer: MockRestServiceServer

    @Test
    fun 상태코드가_4xx이면_InternalServer_예외() {
        // given
        mockServer.expect {
            requestTo(url)
        }.andRespond(
            withBadRequest().contentType(MediaType.APPLICATION_JSON)
        )

        // when
        val ex = shouldThrow<InternalServerException> {
            kakaoOpenIdJwksClient.requestGetJwks()
        }

        // then
        ex shouldHaveMessage ErrorCode.OPEN_ID_PROVIDER_NOT_RESPONSE.message
    }

    @Test
    fun 상태코드가_5xx이면_InternalServer_예외() {
        // given
        mockServer.expect {
            requestTo(url)
        }.andRespond(
            withServerError().contentType(MediaType.APPLICATION_JSON)
        )

        // when
        val ex = shouldThrow<InternalServerException> {
            kakaoOpenIdJwksClient.requestGetJwks()
        }

        // then
        ex shouldHaveMessage ErrorCode.INTERNAL_SERVER_ERROR.message
    }

    @Test
    fun 성공() {
        // given
        val jwksJson = """
            {
                "keys": [
                    {
                        "kid": "3f96980381e451efad0d2ddd30e3d3",
                        "kty": "RSA",
                        "alg": "RS256",
                        "use": "sig",
                        "n": "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw",
                        "e": "AQAB"
                    },
                    {
                        "kid": "9f252dadd5f233f93d2fa528d12fea",
                        "kty": "RSA",
                        "alg": "RS256",
                        "use": "sig",
                        "n": "qGWf6RVzV2pM8YqJ6by5exoixIlTvdXDfYj2v7E6xkoYmesAjp_1IYL7rzhpUYqIkWX0P4wOwAsg-Ud8PcMHggfwUNPOcqgSk1hAIHr63zSlG8xatQb17q9LrWny2HWkUVEU30PxxHsLcuzmfhbRx8kOrNfJEirIuqSyWF_OBHeEgBgYjydd_c8vPo7IiH-pijZn4ZouPsEg7wtdIX3-0ZcXXDbFkaDaqClfqmVCLNBhg3DKYDQOoyWXrpFKUXUFuk2FTCqWaQJ0GniO4p_ppkYIf4zhlwUYfXZEhm8cBo6H2EgukntDbTgnoha8kNunTPekxWTDhE5wGAt6YpT4Yw",
                        "e": "AQAB"
                    }
                ]
            }
        """
        mockServer.expect {
            requestTo(url)
        }.andRespond(
            withSuccess().body(jwksJson).contentType(MediaType.APPLICATION_JSON)
        )

        // when
        val actual = kakaoOpenIdJwksClient.requestGetJwks()

        // then
        actual.getKeys().map { it.id } shouldContainExactlyInAnyOrder listOf(
            "3f96980381e451efad0d2ddd30e3d3",
            "9f252dadd5f233f93d2fa528d12fea"
        )
    }
}
