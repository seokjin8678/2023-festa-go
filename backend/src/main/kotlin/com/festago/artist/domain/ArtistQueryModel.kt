package com.festago.artist.domain

/**
 * 쿼리에서 사용되는 모델이므로, 필드를 추가해도 필드명은 변경되면 절대로 안 됨!!!
 */
data class ArtistQueryModel(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
    val backgroundImageUrl: String,
) {
    companion object {
        fun from(artist: Artist): ArtistQueryModel {
            return ArtistQueryModel(
                artist.identifier,
                artist.name,
                artist.profileImage,
                artist.backgroundImageUrl
            )
        }
    }
}
