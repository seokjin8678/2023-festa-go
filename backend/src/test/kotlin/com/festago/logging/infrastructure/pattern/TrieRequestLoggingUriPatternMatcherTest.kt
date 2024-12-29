package com.festago.logging.infrastructure.pattern

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TrieRequestLoggingUriPatternMatcherTest {

    private lateinit var uriPatternMatcher: TrieRequestLoggingUriPatternMatcher

    @BeforeEach
    fun setUp() {
        uriPatternMatcher = TrieRequestLoggingUriPatternMatcher()
    }

    @Test
    fun 경로가_패턴에_매칭되면_반환값은_null이_아니다() {
        // given
        uriPatternMatcher.addPattern("GET", "/api/v1/schools")

        // when & then
        uriPatternMatcher.match("GET", "/api/v1/schools") shouldNotBe null
    }

    @Test
    fun 경로가_패턴에_매칭되지_않으면_반환값은_null이다() {
        // given
        uriPatternMatcher.addPattern("POST", "/api/v1/schools")

        // when & then
        uriPatternMatcher.match("POST", "/api/v1/festivals") shouldBe null
    }

    @Test
    fun 경로가_매칭되어도_HttpMethod가_매칭되지_않으면_반환값은_null이다() {
        // given
        uriPatternMatcher.addPattern("GET", "/api/v1/schools")

        // when & then
        uriPatternMatcher.match("POST", "/api/v1/schools") shouldBe null
    }

    @Nested
    inner class PathVariable {

        @Test
        fun PathVariable_경로가_패턴에_매칭되어야_한다() {
            // given
            uriPatternMatcher.addPattern("GET", "/api/v1/schools/{schoolId}")

            // when & then
            uriPatternMatcher.match("GET", "/api/v1/schools/1") shouldNotBe true
        }

        @Test
        fun PathVariable_뒤에_경로가_있어도_패턴에_매칭되어야_한다() {
            // given
            uriPatternMatcher.addPattern("GET", "/api/v1/schools/{schoolId}/detail")

            // when & then
            uriPatternMatcher.match("GET", "/api/v1/schools/1/detail") shouldNotBe true
        }
    }

    @Test
    @Disabled
    fun 성능_테스트() {
        // given
        for (i in 1..1000) {
            uriPatternMatcher.addPattern("GET", "/api/v1/schools/$i")
        }

        // when & then
        val start = System.nanoTime()
        val expect = uriPatternMatcher.match("GET", "/api/v1/schools/1000")
        val processTime = (System.nanoTime() - start) / 1_000_000.0
        println("${processTime}ms") // 2020 M1 MacBook Air 기준 0.021ms

        expect shouldNotBe null
    }
}
