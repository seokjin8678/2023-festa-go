package com.festago.artist.repository

import com.festago.artist.domain.QArtist.artist
import com.festago.artist.dto.ArtistDetailV1Response
import com.festago.artist.dto.ArtistFestivalV1Response
import com.festago.artist.dto.QArtistDetailV1Response
import com.festago.artist.dto.QArtistFestivalV1Response
import com.festago.artist.dto.QArtistMediaV1Response
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.domain.QFestivalQueryInfo.festivalQueryInfo
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.QSocialMedia.socialMedia
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.QStageArtist.stageArtist
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import java.time.LocalDate
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
class ArtistDetailV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findArtistDetail(artistId: Long): ArtistDetailV1Response? {
        return queryDslHelper.selectFrom(artist)
            .leftJoin(socialMedia).on(socialMedia.ownerId.eq(artist.id).and(socialMedia.ownerType.eq(OwnerType.ARTIST)))
            .where(artist.id.eq(artistId))
            .transform(
                groupBy(artist.id).list(
                    QArtistDetailV1Response(
                        artist.id,
                        artist.name,
                        artist.profileImage,
                        artist.backgroundImageUrl,
                        list(
                            QArtistMediaV1Response(
                                socialMedia.mediaType,
                                socialMedia.name,
                                socialMedia.logoUrl,
                                socialMedia.url
                            ).skipNulls()
                        )
                    )
                )
            ).firstOrNull()
    }

    fun findArtistFestivals(condition: ArtistFestivalSearchCondition): Slice<ArtistFestivalV1Response> {
        val (artistId, isPast, lastFestivalId, lastStartDate, pageable, currentTime) = condition
        return queryDslHelper.applySlice(pageable) {
            it.select(
                QArtistFestivalV1Response(
                    festival.id,
                    festival.name,
                    festival.festivalDuration.startDate,
                    festival.festivalDuration.endDate,
                    festival.posterImageUrl,
                    festivalQueryInfo.artistInfo
                )
            )
                .from(stageArtist)
                .innerJoin(stage).on(stageArtist.artistId.eq(artistId).and(stage.id.eq(stageArtist.stageId)))
                .innerJoin(festival).on(festival.id.eq(stage.festival.id))
                .leftJoin(festivalQueryInfo).on(festival.id.eq(festivalQueryInfo.festivalId))
                .where(
                    getDynamicWhere(
                        isPast = isPast,
                        lastStartDate = lastStartDate,
                        lastFestivalId = lastFestivalId,
                        currentTime = currentTime
                    )
                )
                .orderBy(*getDynamicOrderBy(isPast))
        }
    }

    private fun getDynamicWhere(
        isPast: Boolean,
        lastStartDate: LocalDate?,
        lastFestivalId: Long?,
        currentTime: LocalDate,
    ): BooleanExpression {
        if (hasCursor(lastStartDate, lastFestivalId)) {
            return getCursorBasedWhere(isPast, lastStartDate, lastFestivalId)
        }
        return getDefaultWhere(isPast, currentTime)
    }

    private fun hasCursor(lastStartDate: LocalDate?, lastFestivalId: Long?): Boolean {
        return lastStartDate != null && lastFestivalId != null
    }

    private fun getCursorBasedWhere(
        isPast: Boolean,
        lastStartDate: LocalDate?,
        lastFestivalId: Long?,
    ): BooleanExpression {
        if (isPast) {
            return festival.festivalDuration.startDate.lt(lastStartDate)
                .or(
                    festival.festivalDuration.startDate.eq(lastStartDate)
                        .and(festival.id.gt(lastFestivalId))
                )
        }
        return festival.festivalDuration.startDate.gt(lastStartDate)
            .or(
                festival.festivalDuration.startDate.eq(lastStartDate)
                    .and(festival.id.gt(lastFestivalId))
            )
    }

    private fun getDefaultWhere(isPast: Boolean, currentTime: LocalDate): BooleanExpression {
        if (isPast) {
            return festival.festivalDuration.endDate.lt(currentTime)
        }
        return festival.festivalDuration.endDate.goe(currentTime)
    }

    private fun getDynamicOrderBy(isPast: Boolean): Array<OrderSpecifier<*>> {
        if (isPast) {
            return arrayOf(festival.festivalDuration.endDate.desc())
        }
        return arrayOf(festival.festivalDuration.startDate.asc(), festival.id.asc())
    }
}
