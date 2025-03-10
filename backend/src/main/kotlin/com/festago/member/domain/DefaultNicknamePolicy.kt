package com.festago.member.domain

import java.util.concurrent.ThreadLocalRandom
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class DefaultNicknamePolicy(
    private val adjectives: List<String>,
    private val nouns: List<String>,
) {

    fun generate(): String {
        val random = ThreadLocalRandom.current()
        val adjective = adjectives[random.nextInt(adjectives.size)]
        val noun = nouns[random.nextInt(nouns.size)]
        return "$adjective $noun"
    }
}

@Configuration
private class DefaultNicknamePolicyConfig {

    @Bean
    fun defaultNicknamePolicy(): DefaultNicknamePolicy {
        return DefaultNicknamePolicy(
            adjectives = listOf(
                "츄러스를 먹는", "노래 부르는", "때창하는", "응원하는",
                "응원봉을 든", "타코야끼를 먹는", "공연에 심취한", "신나는",
                "춤추는", "행복한", "즐거운", "신나는", "흥겨운"
            ),
            nouns = listOf(
                "다람쥐", "토끼", "고양이", "펭귄",
                "캥거루", "사슴", "미어캣", "호랑이",
                "여우", "판다", "고슴도치", "토끼",
                "햄스터", "얼룩말", "너구리", "치타"
            ),
        )
    }
}
