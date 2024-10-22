package com.festago.stage.infrastructure

import com.festago.artist.domain.Artist
import com.festago.artist.domain.QArtist.artist
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.FestivalIdStageArtistsResolver
import com.festago.festival.domain.QFestival.festival
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.QStageArtist.stageArtist
import org.springframework.stereotype.Component

@Component
class FestivalIdStageArtistsQueryDslResolver(
    val queryDslHelper: QueryDslHelper,
) : FestivalIdStageArtistsResolver {

    override fun resolve(festivalId: Long): List<Artist> {
        return queryDslHelper.select(artist)
            .from(festival)
            .join(stage).on(stage.festival.id.eq(festival.id))
            .join(stageArtist).on(stageArtist.stageId.eq(stage.id))
            .join(artist).on(artist.id.eq(stageArtist.artistId))
            .where(festival.id.eq(festivalId))
            .orderBy(artist.id.asc())
            .fetch()
            .distinct()
    }
}
