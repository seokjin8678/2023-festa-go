package com.festago.auth.infrastructure.token.http

import io.kotest.matchers.shouldBe
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest

internal class CookieHttpRequestTokenExtractorTest {

    val cookieRequestTokenExtractor = CookieHttpRequestTokenExtractor()

    @Test
    fun 요청에_쿠키가_없으면_null() {
        // given
        val request = MockHttpServletRequest()

        // when
        val actual = cookieRequestTokenExtractor.extract(request)

        // then
        actual shouldBe null
    }

    @Test
    fun 쿠키에_token_헤더가_없으면_null() {
        // given
        val request = MockHttpServletRequest()
        request.setCookies(Cookie("tokenn", "token"))

        // when
        val actual = cookieRequestTokenExtractor.extract(request)

        // then
        actual shouldBe null
    }

    @Test
    fun 쿠키에_token_헤더가_있으면_값이_반환된다() {
        // given
        val request = MockHttpServletRequest()
        request.setCookies(Cookie("token", "value"))

        // when
        val actual = cookieRequestTokenExtractor.extract(request)

        // then
        actual shouldBe "value"
    }
}
