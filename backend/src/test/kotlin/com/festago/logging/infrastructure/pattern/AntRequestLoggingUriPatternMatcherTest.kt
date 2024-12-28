package com.festago.logging.infrastructure.pattern

import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class AntRequestLoggingUriPatternMatcherTest {

    private lateinit var uriPatternMatcher: AntRequestLoggingUriPatternMatcher

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
        println("${processTime}ms") // 2020 M1 MacBook Air 기준 3.78ms

        expect shouldNotBe null
    }
}
