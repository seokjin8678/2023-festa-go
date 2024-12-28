package com.festago.stage.application

import com.festago.artist.domain.ArtistRepository
import com.festago.artist.domain.ArtistsSerializer
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.stage.domain.Stage
import com.festago.stage.domain.StageQueryInfo
import com.festago.stage.domain.StageQueryInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StageQueryInfoService(
    private val stageQueryInfoRepository: StageQueryInfoRepository,
    private val artistRepository: ArtistRepository,
    private val serializer: ArtistsSerializer,
) {

    fun initialStageQueryInfo(stage: Stage) {
        val artists = artistRepository.findByIdIn(stage.artistIds)
        val stageQueryInfo = StageQueryInfo.of(stage.identifier, artists, serializer)
        stageQueryInfoRepository.save(stageQueryInfo)
    }

    fun renewalStageQueryInfo(stage: Stage) {
        val stageQueryInfo: StageQueryInfo = stageQueryInfoRepository.findByStageId(stage.identifier)
            ?: throw NotFoundException(ErrorCode.STAGE_NOT_FOUND)
        val artists = artistRepository.findByIdIn(stage.artistIds)
        stageQueryInfo.updateArtist(artists, serializer)
    }

    fun deleteStageQueryInfo(stageId: Long) {
        stageQueryInfoRepository.deleteByStageId(stageId)
    }
}
