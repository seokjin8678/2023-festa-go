package com.festago.artist.application

import com.festago.artist.dto.ArtistDetailV1Response
import com.festago.artist.dto.ArtistFestivalV1Response
import com.festago.artist.repository.ArtistDetailV1QueryDslRepository
import com.festago.artist.repository.ArtistFestivalSearchCondition
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import java.time.Clock
import java.time.LocalDate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArtistDetailV1QueryService(
    private val artistDetailV1QueryDslRepository: ArtistDetailV1QueryDslRepository,
    private val clock: Clock,
) {

    fun findArtistDetail(artistId: Long): ArtistDetailV1Response {
        return artistDetailV1QueryDslRepository.findArtistDetail(artistId)
            ?: throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
    }

    fun findArtistFestivals(
        artistId: Long,
        lastFestivalId: Long?,
        lastStartDate: LocalDate?,
        isPast: Boolean,
        pageable: Pageable,
    ): Slice<ArtistFestivalV1Response> {
        return artistDetailV1QueryDslRepository.findArtistFestivals(
            ArtistFestivalSearchCondition(
                artistId = artistId,
                isPast = isPast,
                lastFestivalId = lastFestivalId,
                lastStartDate = lastStartDate,
                pageable = pageable,
                currentTime = LocalDate.now(clock)
            )
        )
    }
}
