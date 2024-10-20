package com.festago.admin.repository

import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.Stage
import org.springframework.stereotype.Repository

@Repository
class AdminStageResolverQueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findStageByFestivalId(festivalId: Long): List<Stage> {
        return queryDslHelper.selectFrom(stage)
            .innerJoin(stage.festival).fetchJoin()
            .leftJoin(stage.artists).fetchJoin()
            .where(festival.id.eq(festivalId))
            .fetch()
    }

    fun findStageByFestivalIdIn(festivalIds: List<Long>): List<Stage> {
        return queryDslHelper.selectFrom(stage)
            .innerJoin(stage.festival).fetchJoin()
            .leftJoin(stage.artists).fetchJoin()
            .where(festival.id.`in`(festivalIds))
            .fetch()
    }
}
