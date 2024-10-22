package com.festago.stage.application

import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistsSerializer
import com.festago.artist.infrastructure.DelimiterArtistsSerializer
import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.MemoryArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.stage.domain.Stage
import com.festago.stage.repository.MemoryStageQueryInfoRepository
import com.festago.stage.repository.MemoryStageRepository
import com.festago.stage.repository.StageQueryInfoRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.StageFixture
import com.festago.support.fixture.StageQueryInfoFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StageQueryInfoServiceTest {

    lateinit var stageQueryInfoService: StageQueryInfoService

    lateinit var stageQueryInfoRepository: StageQueryInfoRepository

    lateinit var stageRepository: StageRepository

    lateinit var artistRepository: ArtistRepository

    var artistsSerializer: ArtistsSerializer = DelimiterArtistsSerializer(",")

    lateinit var 공연: Stage

    lateinit var 뉴진스: Artist

    @BeforeEach
    fun setUp() {
        stageQueryInfoRepository = MemoryStageQueryInfoRepository()
        stageRepository = MemoryStageRepository()
        artistRepository = MemoryArtistRepository()
        stageQueryInfoService = StageQueryInfoService(
            stageQueryInfoRepository,
            artistRepository,
            artistsSerializer
        )
        뉴진스 = artistRepository.save(ArtistFixture.builder().name("뉴진스").build())
        공연 = stageRepository.save(StageFixture.builder().build())
        공연.renewArtists(listOf(뉴진스.identifier))
    }

    @Nested
    inner class initialStageQueryInfo {

        @Test
        fun StageQueryInfo가_생성된다() {
            // when
            stageQueryInfoService.initialStageQueryInfo(공연)

            // then
            stageQueryInfoRepository.findByStageId(공연.identifier) shouldNotBe null
        }
    }

    @Nested
    inner class renewalStageQueryInfo {

        @Test
        fun Stage_식별자에_대한_StageQueryInfo가_없으면_예외() {
            // when
            val ex = shouldThrow<NotFoundException> {
                stageQueryInfoService.renewalStageQueryInfo(공연)
            }

            // then
            ex shouldHaveMessage ErrorCode.STAGE_NOT_FOUND.message
        }

        @Test
        fun StageQueryInfo가_새롭게_갱신된다() {
            // given
            stageQueryInfoRepository.save(
                StageQueryInfoFixture.builder().stageId(공연.id).artistInfo("oldInfo").build()
            )

            // when
            stageQueryInfoService.renewalStageQueryInfo(공연)

            // then
            stageQueryInfoRepository.findByStageId(공연.identifier)!!.artistInfo shouldNotBe "oldInfo"
        }
    }

    @Nested
    inner class deleteStageQueryInfo {

        @Test
        fun StageQueryInfo가_삭제된다() {
            // given
            stageQueryInfoRepository.save(StageQueryInfoFixture.builder().stageId(공연.identifier).build())

            // when
            stageQueryInfoService.deleteStageQueryInfo(공연.identifier)

            // then
            stageQueryInfoRepository.findByStageId(공연.identifier) shouldBe null
        }
    }
}
