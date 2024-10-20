package com.festago.festival.domain

import com.festago.artist.domain.Artist

/**
 * Festival 식별자를 인자로 받고, 해당 Festival 식별자를 참조하는 Stage가 가진 모든 Artist를 반환하는 인터페이스
 */
fun interface FestivalIdStageArtistsResolver {

    fun resolve(festivalId: Long): List<Artist>
}
