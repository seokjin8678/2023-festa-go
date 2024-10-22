package com.festago.stage.domain

import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistsSerializer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class StageQueryInfo(
    id: Long?,
    stageId: Long,
    artistInfo: String?,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    var stageId: Long = stageId
        protected set

    /**
     * 역정규화를 위한 아티스트의 배열 JSON 컬럼
     */
    @Column(columnDefinition = "TEXT")
    var artistInfo: String = artistInfo ?: "[]"
        protected set

    constructor(stageId: Long, artistInfo: String?) : this(
        id = null,
        stageId = stageId,
        artistInfo = artistInfo
    )

    fun updateArtist(artists: List<Artist>, serializer: ArtistsSerializer) {
        this.artistInfo = serializer.serialize(artists)
    }

    companion object {
        @JvmStatic
        fun of(stageId: Long, artists: List<Artist>, serializer: ArtistsSerializer): StageQueryInfo {
            return StageQueryInfo(stageId, serializer.serialize(artists))
        }
    }
}
