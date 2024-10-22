package com.festago.support

import io.mockk.spyk
import java.time.Clock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestTimeConfigKt {

    @Bean("testClock")
    @Primary
    fun clock(): Clock {
        return spyk(Clock.systemDefaultZone())
    }
}