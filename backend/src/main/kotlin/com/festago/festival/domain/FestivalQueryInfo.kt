package com.festago.festival.domain

import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistsSerializer
import com.festago.common.domain.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class FestivalQueryInfo private constructor(
    festivalId: Long,
    artistInfo: String
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(unique = true, nullable = false)
    var festivalId: Long = festivalId
        protected set

    @Column(columnDefinition = "TEXT", nullable = false)
    var artistInfo: String = artistInfo
        protected set

    fun updateArtistInfo(artists: List<Artist>, serializer: ArtistsSerializer) {
        this.artistInfo = serializer.serialize(artists)
    }

    companion object {

        @JvmStatic
        fun create(festivalId: Long): FestivalQueryInfo {
            return FestivalQueryInfo(festivalId, "[]")
        }
    }
}
