package com.festago.auth.infrastructure.token.http

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest

internal class HeaderHttpRequestTokenExtractorTest {

    val headerHttpRequestTokenExtractor = HeaderHttpRequestTokenExtractor()

    @Test
    fun 요청에_Authorization_헤더가_없으면_null() {
        // given
        val request = MockHttpServletRequest()

        // when
        val actual = headerHttpRequestTokenExtractor.extract(request)

        // then
        actual shouldBe null
    }

    @Test
    fun Bearer_토큰이_아니면_예외() {
        // given
        val request = MockHttpServletRequest()
        request.addHeader(HttpHeaders.AUTHORIZATION, "1234")

        // when
        val ex = shouldThrow<UnauthorizedException> {
            headerHttpRequestTokenExtractor.extract(request)
        }

        // then
        ex shouldHaveMessage ErrorCode.NOT_BEARER_TOKEN_TYPE.message
    }

    @Test
    fun Bearer_토큰이면_값이_반환된다() {
        // given
        val request = MockHttpServletRequest()
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token")

        // when
        val actual = headerHttpRequestTokenExtractor.extract(request)

        // then
        actual shouldBe "token"
    }
}