package com.festago.artist.infrastructure.repository.query

import com.festago.artist.domain.QArtist.artist
import com.festago.artist.domain.QArtistAlias.artistAlias
import com.festago.artist.dto.ArtistSearchV1Response
import com.festago.artist.dto.QArtistSearchV1Response
import com.festago.common.querydsl.QueryDslHelper
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.QStageArtist.stageArtist
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import java.time.LocalDateTime
import org.springframework.stereotype.Repository

@Repository
class ArtistSearchV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findAllByLike(keyword: String): List<ArtistSearchV1Response> {
        return queryDslHelper.select(
            QArtistSearchV1Response(
                artist.id,
                artist.name,
                artist.profileImage
            )
        )
            .from(artist)
            .innerJoin(artistAlias).on(artistAlias.artistId.eq(artist.id))
            .where(artistAlias.alias.contains(keyword))
            .fetch()
            .sortedBy { it.name }
    }

    fun findAllByEqual(keyword: String): List<ArtistSearchV1Response> {
        return queryDslHelper.select(
            QArtistSearchV1Response(
                artist.id,
                artist.name,
                artist.profileImage
            )
        )
            .from(artist)
            .innerJoin(artistAlias).on(artistAlias.artistId.eq(artist.id))
            .where(artistAlias.alias.eq(keyword))
            .fetch()
            .sortedBy { it.name }
    }

    /**
     * artistIds와 LocalDateTime을 받고, 각 aritstId가 LocalDateTime 이후에 참여할 공연의 시간들을 반환한다.
     */
    fun findArtistsStageScheduleAfterStageStartTime(
        artistIds: List<Long>,
        dateTime: LocalDateTime,
    ): Map<Long, List<LocalDateTime>> {
        return queryDslHelper.selectFrom(stageArtist)
            .innerJoin(stage).on(stage.id.eq(stageArtist.stageId))
            .where(stageArtist.artistId.`in`(artistIds).and(stage.startTime.goe(dateTime)))
            .transform(groupBy(stageArtist.artistId).`as`(list(stage.startTime)))
    }
}
