package com.festago.artist.domain

import com.festago.common.domain.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ArtistAlias(
    artistId: Long,
    alias: String,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var artistId: Long = artistId
        protected set

    var alias: String = alias
        protected set

    fun updateAlias(alias: String) {
        this.alias = alias
    }
}
