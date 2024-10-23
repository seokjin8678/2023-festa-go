package com.festago.stage.repository

import com.festago.stage.domain.StageArtist
import com.festago.support.AbstractMemoryRepository

class MemoryStageArtistRepository : AbstractMemoryRepository<StageArtist>(), StageArtistRepository
