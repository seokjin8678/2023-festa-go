package com.festago.artist.application

import com.festago.artist.dto.ArtistSearchStageCountV1Response
import com.festago.artist.dto.ArtistSearchV1Response
import com.festago.artist.dto.ArtistTotalSearchV1Response
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.time.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.junit.jupiter.api.Test

@DisplayNameGeneration(ReplaceUnderscores::class)
internal class ArtistTotalSearchV1ServiceTest {

    lateinit var artistSearchV1QueryService: ArtistSearchV1QueryService

    lateinit var artistSearchStageCountV1QueryService: ArtistSearchStageCountV1QueryService

    lateinit var clock: Clock

    lateinit var artistTotalSearchV1Service: ArtistTotalSearchV1Service

    @BeforeEach
    fun setUp() {
        artistSearchV1QueryService = mockk()
        artistSearchStageCountV1QueryService = mockk()
        clock = spyk(Clock.systemDefaultZone())
        artistTotalSearchV1Service = ArtistTotalSearchV1Service(
            artistSearchV1QueryService,
            artistSearchStageCountV1QueryService,
            clock
        )
    }

    @Test
    fun 아티스트_정보와_공연_일정을_종합하여_반환한다() {
        val artists = listOf(
            ArtistSearchV1Response(1L, "아이브", "www.IVE-image.png"),
            ArtistSearchV1Response(2L, "아이유", "www.IU-image.png"),
            ArtistSearchV1Response(3L, "(여자)아이들", "www.IDLE-image.png")
        )
        every { artistSearchV1QueryService.findAllByKeyword(any<String>()) } returns artists

        val artistToStageSchedule = mapOf(
            1L to ArtistSearchStageCountV1Response(1, 0),
            2L to ArtistSearchStageCountV1Response(0, 0),
            3L to ArtistSearchStageCountV1Response(0, 2)
        )
        every {
            artistSearchStageCountV1QueryService.findArtistsStageCountAfterDateTime(
                artistIds = any(),
                now = any()
            )
        } returns artistToStageSchedule

        // when
        val actual = artistTotalSearchV1Service.findAllByKeyword("아이")

        // then
        val expected = listOf(
            ArtistTotalSearchV1Response(1L, "아이브", "www.IVE-image.png", 1, 0),
            ArtistTotalSearchV1Response(2L, "아이유", "www.IU-image.png", 0, 0),
            ArtistTotalSearchV1Response(3L, "(여자)아이들", "www.IDLE-image.png", 0, 2)
        )
        actual shouldBe expected
    }

    @Test
    fun 검색_결과가_해당하는_아티스트가_없으면_빈리스트를_반환한다() {
        // given
        every { artistSearchV1QueryService.findAllByKeyword(any<String>()) } returns emptyList()
        every {
            artistSearchStageCountV1QueryService.findArtistsStageCountAfterDateTime(
                artistIds = any(),
                now = any()
            )
        } returns emptyMap()

        // when
        val actual = artistTotalSearchV1Service.findAllByKeyword("없어")

        // then
        actual shouldHaveSize 0
    }
}
