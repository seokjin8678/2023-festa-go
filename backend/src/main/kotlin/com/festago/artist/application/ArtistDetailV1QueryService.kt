package com.festago.artist.application

import com.festago.artist.dto.ArtistDetailV1Response
import com.festago.artist.dto.ArtistFestivalV1Response
import com.festago.artist.infrastructure.repository.query.ArtistDetailV1QueryDslRepository
import com.festago.artist.infrastructure.repository.query.ArtistFestivalSearchCondition
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import java.time.Clock
import java.time.LocalDate
import org.springframework.cache.annotation.Cacheable
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

    @Cacheable(cacheNames = [ARTIST_DETAIL_CACHE_NAME], key = "#artistId")
    fun findArtistDetail(artistId: Long): ArtistDetailV1Response {
        return artistDetailV1QueryDslRepository.findArtistDetail(artistId)
            ?: throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
    }

    @Cacheable(
        cacheNames = [ARTIST_DETAIL_FESTIVALS_CACHE_NAME],
        key = "#artistId",
        condition = "#lastFestivalId == null && #lastStartDate == null && #isPast == false && #pageable.pageSize == 10"
    )
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

    companion object {
        const val ARTIST_DETAIL_CACHE_NAME = "ARTIST_DETAIL_V1"
        const val ARTIST_DETAIL_FESTIVALS_CACHE_NAME = "ARTIST_DETAIL_FESTIVALS_V1"
    }
}
