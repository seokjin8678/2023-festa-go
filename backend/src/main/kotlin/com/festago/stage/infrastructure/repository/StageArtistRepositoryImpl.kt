package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.StageArtist
import com.festago.stage.domain.StageArtistRepository
import org.springframework.stereotype.Repository

@Repository
internal class StageArtistRepositoryImpl(
    private val stageArtistJpaRepository: StageArtistJpaRepository,
) : StageArtistRepository {

    override fun save(stageArtist: StageArtist): StageArtist {
        return stageArtistJpaRepository.save(stageArtist)
    }
}
