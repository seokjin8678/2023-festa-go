package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.StageArtist
import com.festago.stage.domain.StageArtistRepository
import com.festago.support.AbstractMemoryRepository

class MemoryStageArtistRepository : AbstractMemoryRepository<StageArtist>(), StageArtistRepository
