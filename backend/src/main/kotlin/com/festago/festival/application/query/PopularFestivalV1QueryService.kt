package com.festago.festival.application.query

import com.festago.festival.dto.PopularFestivalsV1Response
import com.festago.festival.infrastructure.repository.query.PopularFestivalV1QueryDslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PopularFestivalV1QueryService(
    private val popularFestivalRepository: PopularFestivalV1QueryDslRepository,
) {

    fun findPopularFestivals(): PopularFestivalsV1Response {
        val popularFestivals = popularFestivalRepository.findPopularFestivals()
        return PopularFestivalsV1Response("요즘 뜨는 축제", popularFestivals)
    }
}
