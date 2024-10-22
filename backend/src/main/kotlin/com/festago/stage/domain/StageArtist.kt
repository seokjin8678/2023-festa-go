package com.festago.stage.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class StageArtist(
    id: Long?,
    stageId: Long,
    artistId: Long,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    var stageId: Long = stageId
        protected set

    var artistId: Long = artistId
        protected set

    constructor(stageId: Long, artistId: Long) : this(null, stageId, artistId)
}
