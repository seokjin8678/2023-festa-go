package com.festago.festival.application

import com.festago.artist.domain.ArtistsSerializer
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.domain.FestivalIdStageArtistsResolver
import com.festago.festival.domain.FestivalQueryInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FestivalQueryInfoArtistRenewService(
    private val festivalQueryInfoRepository: FestivalQueryInfoRepository,
    private val festivalIdStageArtistsResolver: FestivalIdStageArtistsResolver,
    private val serializer: ArtistsSerializer,
) {

    fun renewArtistInfo(festivalId: Long) {
        val festivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(festivalId)
            ?: throw NotFoundException(ErrorCode.FESTIVAL_NOT_FOUND)
        val artists = festivalIdStageArtistsResolver.resolve(festivalId)
        festivalQueryInfo.updateArtistInfo(artists, serializer)
    }
}
