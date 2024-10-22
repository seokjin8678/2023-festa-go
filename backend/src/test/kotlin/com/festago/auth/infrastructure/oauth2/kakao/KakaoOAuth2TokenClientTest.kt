package com.festago.auth.infrastructure.oauth2.kakao

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest

@RestClientTest(KakaoOAuth2TokenClient::class)
internal class KakaoOAuth2TokenClientTest {

    @Autowired
    lateinit var kakaoOAuth2TokenClient: KakaoOAuth2TokenClient

    @Autowired
    lateinit var mockServer: MockRestServiceServer

    @Test
    fun 상태코드_400에서_KOE320_에러코드_이면_BadRequest_예외() {
        // given
        val body = """
            {
                "error": "error",
                "error_description": "errorDescription",
                "error_code": "KOE320"
            }
        """
        mockServer.expect(requestTo(URL))
            .andRespond(
                withBadRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
            )

        // when
        val ex = shouldThrow<BadRequestException> {
            kakaoOAuth2TokenClient.getIdToken("code")
        }

        // then
        ex shouldHaveMessage ErrorCode.OAUTH2_INVALID_CODE.message
    }

    @Test
    fun 상태코드_400에서_KOE320_에러코드가_아니면_InternalServer_예외() {
        // given
        val body = """
            {
                "error": "error",
                "error_description": "errorDescription",
                "error_code": "ERROR"
            }
        """
        mockServer.expect(requestTo(URL))
            .andRespond(
                withBadRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
            )

        // when
        val ex = shouldThrow<InternalServerException> {
            kakaoOAuth2TokenClient.getIdToken("code")
        }

        // then
        ex shouldHaveMessage ErrorCode.OAUTH2_INVALID_REQUEST.message
    }

    @Test
    fun 상태코드가_401이면_InternalServer_예외() {
        // given
        mockServer.expect(requestTo(URL))
            .andRespond(
                withUnauthorizedRequest()
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // when
        val ex = shouldThrow<InternalServerException> {
            kakaoOAuth2TokenClient.getIdToken("code")
        }

        // then
        ex shouldHaveMessage ErrorCode.OAUTH2_INVALID_REQUEST.message
    }

    @Test
    fun 상태코드가_500이면_InternalServer_예외() {
        // given
        mockServer.expect(requestTo(URL))
            .andRespond(
                withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // when
        val ex = shouldThrow<InternalServerException> {
            kakaoOAuth2TokenClient.getIdToken("code")
        }

        // then
        ex shouldHaveMessage ErrorCode.OAUTH2_PROVIDER_NOT_RESPONSE.message
    }

    @Test
    fun 성공() {
        // given
        val body = """
            {
                "access_token": "accessToken",
                "id_token": "idToken"
            }
        """
        mockServer.expect { requestTo(URL) }
            .andRespond(
                withSuccess()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
            )

        // when
        val actual = kakaoOAuth2TokenClient.getIdToken("code")

        // then
        actual shouldBe "idToken"
    }

    companion object {
        private const val URL = "https://kauth.kakao.com/oauth/token"
    }
}
