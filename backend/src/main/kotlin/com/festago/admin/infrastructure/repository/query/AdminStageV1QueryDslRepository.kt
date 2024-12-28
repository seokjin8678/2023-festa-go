package com.festago.admin.infrastructure.repository.query

import com.festago.admin.dto.stage.AdminStageV1Response
import com.festago.admin.dto.stage.QAdminStageArtistV1Response
import com.festago.admin.dto.stage.QAdminStageV1Response
import com.festago.artist.domain.QArtist.artist
import com.festago.common.querydsl.QueryDslHelper
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.QStageArtist.stageArtist
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import org.springframework.stereotype.Repository

@Repository
class AdminStageV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findAllByFestivalId(festivalId: Long): List<AdminStageV1Response> {
        return queryDslHelper.selectFrom(stage)
            .innerJoin(stageArtist).on(stageArtist.stageId.eq(stage.id))
            .innerJoin(artist).on(artist.id.eq(stageArtist.artistId))
            .where(stage.festival.id.eq(festivalId))
            .orderBy(stage.startTime.asc())
            .transform(
                groupBy(stage.id).list(
                    QAdminStageV1Response(
                        stage.id,
                        stage.startTime,
                        stage.ticketOpenTime,
                        list(
                            QAdminStageArtistV1Response(
                                artist.id,
                                artist.name
                            ).skipNulls()
                        ),
                        stage.createdAt,
                        stage.updatedAt
                    )
                )
            )
    }

    fun findById(stageId: Long): AdminStageV1Response? {
        return queryDslHelper.selectFrom(stage)
            .leftJoin(stageArtist).on(stageArtist.stageId.eq(stageId))
            .leftJoin(artist).on(artist.id.eq(stageArtist.artistId))
            .where(stage.id.eq(stageId))
            .transform(
                groupBy(stage.id).list(
                    QAdminStageV1Response(
                        stage.id,
                        stage.startTime,
                        stage.ticketOpenTime,
                        list(
                            QAdminStageArtistV1Response(
                                artist.id,
                                artist.name
                            )
                        ),
                        stage.createdAt,
                        stage.updatedAt
                    )
                )
            ).firstOrNull()
    }
}
