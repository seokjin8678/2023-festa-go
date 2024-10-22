package com.festago.stage.repository

import com.festago.stage.domain.StageArtist
import com.festago.support.AbstractMemoryRepositoryKt

class MemoryStageArtistRepository : AbstractMemoryRepositoryKt<StageArtist>(), StageArtistRepository
