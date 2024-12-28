package com.festago.stage.domain

interface StageArtistRepository {

    fun save(stageArtist: StageArtist): StageArtist
}
