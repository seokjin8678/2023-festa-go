package com.festago.stage.repository

import com.festago.stage.domain.StageArtist
import org.springframework.data.repository.Repository

interface StageArtistRepository : Repository<StageArtist, Long> {

    fun save(stageArtist: StageArtist): StageArtist
}
