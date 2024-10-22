package com.festago.artist.presentation.v1

import com.festago.artist.application.ArtistDetailV1QueryService
import com.festago.artist.dto.ArtistDetailV1Response
import com.festago.artist.dto.ArtistFestivalV1Response
import com.festago.common.aop.ValidPageable
import com.festago.common.dto.SliceResponse
import com.festago.common.exception.ValidException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "아티스트 정보 요청 V1")
class ArtistV1Controller(
    private val artistDetailV1QueryService: ArtistDetailV1QueryService,
) {

    @GetMapping("/{artistId}")
    @Operation(description = "아티스트의 정보를 조회한다.", summary = "아티스트 정보 조회")
    fun findArtistDetail(@PathVariable artistId: Long): ResponseEntity<ArtistDetailV1Response> {
        return ResponseEntity.ok(artistDetailV1QueryService.findArtistDetail(artistId))
    }

    @ValidPageable(maxSize = 20)
    @GetMapping("/{artistId}/festivals")
    @Operation(
        description = "아티스트가 참여한 축제 목록을 조회한다. isPast 값으로 종료 축제와 진행, 예정 축제를 구분 가능하다.",
        summary = "아티스트 참여 축제 목록 조회"
    )
    fun findArtistFestivals(
        @PathVariable artistId: Long,
        @RequestParam lastFestivalId: Long?,
        @RequestParam lastStartDate: LocalDate?,
        @RequestParam isPast: Boolean = false,
        @RequestParam @Parameter(description = "0 < size <= 20") size: Int = 10,
    ): ResponseEntity<SliceResponse<ArtistFestivalV1Response>> {
        validateCursor(lastFestivalId, lastStartDate)
        val response = artistDetailV1QueryService.findArtistFestivals(
            artistId = artistId,
            lastFestivalId = lastFestivalId,
            lastStartDate = lastStartDate,
            isPast = isPast,
            pageable = PageRequest.ofSize(size)
        )
        return ResponseEntity.ok(SliceResponse.from(response))
    }

    private fun validateCursor(lastFestivalId: Long?, lastStartDate: LocalDate?) {
        if (lastFestivalId == null && lastStartDate == null) {
            return
        }
        if (lastFestivalId != null && lastStartDate != null) {
            return
        }
        throw ValidException("festivalId, lastStartDate 두 값 모두 요청하거나 요청하지 않아야합니다.")
    }
}
