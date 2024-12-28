package com.festago.festival.application

import com.festago.artist.domain.Artist
import com.festago.festival.domain.FestivalIdStageArtistsResolver
import com.festago.festival.infrastructure.repository.MemoryFestivalQueryInfoRepository
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalQueryInfoFixture
import com.festago.support.spec.UnitDescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class FestivalQueryInfoArtistRenewServiceTest : UnitDescribeSpec({
    val festivalQueryInfoRepository = MemoryFestivalQueryInfoRepository()
    val festivalIdStageArtistsResolver = mockk<FestivalIdStageArtistsResolver>()
    val serializer: (artists: List<Artist>) -> String = { artist -> artist.joinToString(separator = ",") { it.name } }
    val festivalQueryInfoArtistRenewService = FestivalQueryInfoArtistRenewService(
        festivalQueryInfoRepository = festivalQueryInfoRepository,
        festivalIdStageArtistsResolver = festivalIdStageArtistsResolver,
        serializer = serializer
    )

    describe("ArtistInfo 재갱신") {
        val festivalId = 1L
        val 뉴진스 = ArtistFixture.builder().id(1L).name("뉴진스").build()
        val 소녀시대 = ArtistFixture.builder().id(2L).name("소녀시대").build()

        every { festivalIdStageArtistsResolver.resolve(any(Long::class)) } returns listOf(뉴진스, 소녀시대)

        festivalQueryInfoRepository.save(
            FestivalQueryInfoFixture.builder()
                .festivalId(festivalId)
                .build()
        )

        context("ArtistInfo를 재갱신하면") {
            festivalQueryInfoArtistRenewService.renewArtistInfo(festivalId)

            it("저장된 ArtistInfo가 갱신되어야 한다") {
                val actual = festivalQueryInfoRepository.findByFestivalId(festivalId)!!
                actual.artistInfo shouldBe "뉴진스,소녀시대"
            }
        }
    }
})
