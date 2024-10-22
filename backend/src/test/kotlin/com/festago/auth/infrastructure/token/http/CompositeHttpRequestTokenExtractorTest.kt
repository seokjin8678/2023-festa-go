package com.festago.auth.infrastructure.token.http

import com.festago.auth.domain.token.http.HttpRequestTokenExtractor
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest

internal class CompositeHttpRequestTokenExtractorTest {

    @Test
    fun HttpRequestTokenExtractors_모두_null을_반환하면_null을_반환한다() {
        // given
        val req = MockHttpServletRequest()
        val compositeHttpRequestTokenExtractor = CompositeHttpRequestTokenExtractor(
            listOf(
                HttpRequestTokenExtractor { null },
                HttpRequestTokenExtractor { null },
            )
        )

        // when
        val actual = compositeHttpRequestTokenExtractor.extract(req)

        // then
        actual shouldBe null
    }


    @Test
    fun HttpRequestTokenExtractors_중_하나라도_null이_아닌_값을_반환하면_해당_값을_반환한다() {
        // given
        val req = MockHttpServletRequest()
        val compositeHttpRequestTokenExtractor = CompositeHttpRequestTokenExtractor(
            listOf(
                HttpRequestTokenExtractor { null },
                HttpRequestTokenExtractor { "hello" },
            )
        )

        // when
        val actual = compositeHttpRequestTokenExtractor.extract(req)

        // then
        actual shouldBe "hello"
    }
}