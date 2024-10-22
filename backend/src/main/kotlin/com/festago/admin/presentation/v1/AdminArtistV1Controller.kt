package com.festago.admin.presentation.v1

import com.festago.admin.application.AdminArtistV1QueryService
import com.festago.admin.dto.artist.AdminArtistV1Response
import com.festago.admin.dto.artist.ArtistV1CreateRequest
import com.festago.admin.dto.artist.ArtistV1UpdateRequest
import com.festago.artist.application.ArtistCommandService
import com.festago.common.aop.ValidPageable
import com.festago.common.querydsl.SearchCondition
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import java.net.URI
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/v1/artists")
class AdminArtistV1Controller(
    private val artistCommandService: ArtistCommandService,
    private val artistV1QueryService: AdminArtistV1QueryService,
) {

    @PostMapping
    fun create(@Valid @RequestBody request: ArtistV1CreateRequest): ResponseEntity<Void> {
        val artistId = artistCommandService.save(request.toCommand())
        return ResponseEntity.created(URI.create("/admin/api/v1/artists/$artistId"))
            .build()
    }

    @PutMapping("/{artistId}")
    fun update(
        @PathVariable artistId: Long,
        @Valid @RequestBody request: ArtistV1UpdateRequest,
    ): ResponseEntity<Void> {
        artistCommandService.update(request.toCommand(), artistId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{artistId}")
    fun delete(@PathVariable artistId: Long): ResponseEntity<Void> {
        artistCommandService.delete(artistId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{artistId}")
    fun findById(@PathVariable artistId: Long): ResponseEntity<AdminArtistV1Response> {
        return ResponseEntity.ok()
            .body(artistV1QueryService.findById(artistId))
    }

    @GetMapping
    @ValidPageable(maxSize = 50)
    fun findAll(
        @RequestParam searchFilter: String = "",
        @RequestParam searchKeyword: String = "",
        @PageableDefault(size = 10) pageable: Pageable,
    ): ResponseEntity<Page<AdminArtistV1Response>> {
        return ResponseEntity.ok()
            .body(artistV1QueryService.findAll(SearchCondition(searchFilter, searchKeyword, pageable)))
    }
}

