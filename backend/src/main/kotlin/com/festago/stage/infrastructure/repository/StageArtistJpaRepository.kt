package com.festago.stage.infrastructure.repository

import com.festago.stage.domain.StageArtist
import org.springframework.data.jpa.repository.JpaRepository

internal interface StageArtistJpaRepository : JpaRepository<StageArtist, Long>
