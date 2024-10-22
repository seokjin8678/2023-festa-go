package com.festago.festival.repository

import com.festago.common.exception.UnexpectedException
import com.festago.common.querydsl.QueryDslHelper
import com.festago.festival.domain.QFestival.festival
import com.festago.festival.dto.FestivalDetailV1Response
import com.festago.festival.dto.FestivalStageV1Response
import com.festago.festival.dto.QFestivalDetailV1Response
import com.festago.festival.dto.QFestivalStageV1Response
import com.festago.festival.dto.QSchoolV1Response
import com.festago.festival.dto.QSocialMediaV1Response
import com.festago.festival.dto.SocialMediaV1Response
import com.festago.school.domain.QSchool.school
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.QSocialMedia.socialMedia
import com.festago.stage.domain.QStage.stage
import com.festago.stage.domain.QStageQueryInfo.stageQueryInfo
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.sortedSet
import org.springframework.stereotype.Repository

@Repository
class FestivalDetailV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {
    /**
     * 축제에 3개의 공연과 2개의 소셜미디어가 있을 때 조회되는 row 수는 다음과 같다.
     *
     * 1(축제) * 3(공연) * 2(소셜미디어) = 6 row
     *
     * 따라서 중복된 row가 생기게 되는데, 이를 해결하기 위해 set을 사용했고, 항상 일관되게 정렬된 데이터를 조회하기 위해 sortedSet을 사용했음
     */
    fun findFestivalDetail(festivalId: Long): FestivalDetailV1Response? {
        val response = queryDslHelper.selectFrom(festival)
            .innerJoin(school).on(school.id.eq(festival.school.id))
            .leftJoin(socialMedia).on(
                socialMedia.ownerId.eq(school.id)
                    .and(socialMedia.ownerType.eq(OwnerType.SCHOOL))
            )
            .leftJoin(stage).on(stage.festival.id.eq(festival.id))
            .leftJoin(stageQueryInfo).on(stageQueryInfo.stageId.eq(stage.id))
            .where(festival.id.eq(festivalId))
            .transform(
                groupBy(festival.id).list(
                    QFestivalDetailV1Response(
                        festival.id,
                        festival.name,
                        QSchoolV1Response(
                            school.id,
                            school.name
                        ),
                        festival.festivalDuration.startDate,
                        festival.festivalDuration.endDate,
                        festival.posterImageUrl,
                        sortedSet(
                            QSocialMediaV1Response(
                                socialMedia.mediaType,
                                socialMedia.name,
                                socialMedia.logoUrl,
                                socialMedia.url
                            ).skipNulls(), Comparator.comparing(SocialMediaV1Response::name)
                        ),
                        sortedSet(
                            QFestivalStageV1Response(
                                stage.id,
                                stage.startTime,
                                stageQueryInfo.artistInfo
                            ).skipNulls(), Comparator.comparing(FestivalStageV1Response::startDateTime)
                        )
                    )
                )
            )
        // PK로 조회하기에 발생할 일이 없는 예외지만, 혹시 모를 상황을 방지하기 위함
        if (response.size >= 2) {
            throw UnexpectedException("축제 상세 조회에서 2개 이상의 축제가 조회되었습니다.")
        }
        return response.firstOrNull()
    }
}
