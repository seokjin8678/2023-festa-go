package com.festago.stage.application.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.festival.repository.FestivalQueryInfoRepository
import com.festago.festival.repository.FestivalRepository
import com.festago.school.domain.SchoolRegion
import com.festago.school.repository.SchoolRepository
import com.festago.stage.domain.StageQueryInfo
import com.festago.stage.dto.command.StageCreateCommand
import com.festago.stage.dto.command.StageUpdateCommand
import com.festago.stage.repository.StageQueryInfoRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.TimeInstantProvider
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.FestivalQueryInfoFixture
import com.festago.support.fixture.SchoolFixture
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired

class StageCommandServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var stageCreateService: StageCreateService

    @Autowired
    lateinit var stageUpdateService: StageUpdateService

    @Autowired
    lateinit var stageDeleteService: StageDeleteService

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var festivalQueryInfoRepository: FestivalQueryInfoRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var stageQueryInfoRepository: StageQueryInfoRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var clock: Clock

    var now: LocalDateTime = LocalDateTime.parse("2077-06-29T18:00:00")
    var festivalStartDate: LocalDate = LocalDate.parse("2077-06-30")
    var festivalEndDate: LocalDate = LocalDate.parse("2077-07-02")
    var 테코대학교_식별자: Long = 0
    var 테코대학교_축제_식별자: Long = 0
    var 에픽하이_식별자: Long = 0
    var 소녀시대_식별자: Long = 0
    var 뉴진스_식별자: Long = 0

    @BeforeEach
    fun setUp() {
        given(clock.instant())
            .willReturn(TimeInstantProvider.from(now))
        val 테코대학교 = schoolRepository.save(
            SchoolFixture.builder()
                .name("테코대학교")
                .region(SchoolRegion.서울)
                .build()
        )
        테코대학교_식별자 = 테코대학교.identifier
        테코대학교_축제_식별자 = festivalRepository.save(
            FestivalFixture.builder()
                .name("테코대학교 축제")
                .startDate(festivalStartDate)
                .endDate(festivalEndDate)
                .school(테코대학교)
                .build()
        ).identifier
        festivalQueryInfoRepository.save(FestivalQueryInfoFixture.builder().festivalId(테코대학교_축제_식별자).build())

        에픽하이_식별자 = artistRepository.save(ArtistFixture.builder().name("에픽하이").build()).identifier
        소녀시대_식별자 = artistRepository.save(ArtistFixture.builder().name("소녀시대").build()).identifier
        뉴진스_식별자 = artistRepository.save(ArtistFixture.builder().name("뉴진스").build()).identifier
    }

    @Nested
    inner class createStage {

        @Test
        fun 공연을_생성하면_StageQueryInfo가_저장된다() {
            // given
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제_식별자,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
            )

            // when
            val stageId = stageCreateService.createStage(command)

            // then
            stageQueryInfoRepository.findByStageId(stageId) shouldNotBe null
        }

        @Test
        fun 공연을_생성하면_FestivalQueryInfo가_갱신된다() {
            // given
            val command = StageCreateCommand(
                festivalId = 테코대학교_축제_식별자,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
            )

            // when
            val previousFestivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(테코대학교_축제_식별자)!!
            val stageId = stageCreateService.createStage(command)

            // then
            val festivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(테코대학교_축제_식별자)!!
            val stageQueryInfo: StageQueryInfo = stageQueryInfoRepository.findByStageId(stageId)!!

            festivalQueryInfo.artistInfo shouldNotBe previousFestivalQueryInfo.artistInfo
            festivalQueryInfo.artistInfo shouldBe stageQueryInfo.artistInfo
        }

        @Test
        fun 공연이_여러_개_추가되면_FestivalQueryInfo에_추가된_공연의_ArtistInfo가_갱신된다() {
            // given
            val firstCommand = StageCreateCommand(
                festivalId = 테코대학교_축제_식별자,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자)
            )
            val secondCommand = StageCreateCommand(
                festivalId = 테코대학교_축제_식별자,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(소녀시대_식별자)
            )

            // when
            stageCreateService.createStage(firstCommand)
            stageCreateService.createStage(secondCommand)

            // then
            val festivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(테코대학교_축제_식별자)!!
            val actual = objectMapper.readValue<Array<Artist>>(festivalQueryInfo.artistInfo)

            actual.map { it.id } shouldContainExactlyInAnyOrder listOf(에픽하이_식별자, 소녀시대_식별자)
        }

        @Test
        fun 공연이_여러_개_추가될때_공연에_중복된_아티스트가_있어도_FestivalQueryInfo에는_중복이_없다() {
            // given
            val firstCommand = StageCreateCommand(
                festivalId = 테코대학교_축제_식별자,
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
            )

            val secondCommand = StageCreateCommand(
                festivalId = 테코대학교_축제_식별자,
                startTime = festivalStartDate.plusDays(1).atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
            )

            // when
            stageCreateService.createStage(firstCommand)
            stageCreateService.createStage(secondCommand)

            // then
            val festivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(테코대학교_축제_식별자)!!
            val actual = objectMapper.readValue<Array<Artist>>(festivalQueryInfo.artistInfo)
            actual.map { it.id } shouldContainExactlyInAnyOrder listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
        }
    }

    @Nested
    inner class updateStage {
        var 테코대학교_축제_공연_식별자: Long = 0

        @BeforeEach
        fun setUp() {
            테코대학교_축제_공연_식별자 = stageCreateService.createStage(
                StageCreateCommand(
                    festivalId = 테코대학교_축제_식별자,
                    startTime = festivalStartDate.atTime(18, 0),
                    ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                    artistIds = listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
                )
            )
        }

        @Test
        fun 공연을_수정하면_StageQueryInfo가_갱신된다() {
            // given
            val command = StageUpdateCommand(
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자, 소녀시대_식별자)
            )

            // when
            stageUpdateService.updateStage(테코대학교_축제_공연_식별자, command)

            // then
            val stageQueryInfo = stageQueryInfoRepository.findByStageId(테코대학교_축제_공연_식별자)!!
            val actual = objectMapper.readValue<Array<Artist>>(stageQueryInfo.artistInfo)
            actual.map { it.id } shouldContainExactlyInAnyOrder listOf(에픽하이_식별자, 소녀시대_식별자)
        }

        @Test
        fun 공연을_수정하면_FestivalQueryInfo가_갱신된다() {
            // given
            val command = StageUpdateCommand(
                startTime = festivalStartDate.atTime(18, 0),
                ticketOpenTime = festivalStartDate.minusWeeks(1).atStartOfDay(),
                artistIds = listOf(에픽하이_식별자, 소녀시대_식별자)
            )

            // when
            stageUpdateService.updateStage(테코대학교_축제_공연_식별자, command)

            val festivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(테코대학교_축제_식별자)!!
            val actual = objectMapper.readValue<Array<Artist>>(festivalQueryInfo.artistInfo)
            actual.map { it.id } shouldContainExactlyInAnyOrder listOf(에픽하이_식별자, 소녀시대_식별자)
        }
    }

    @Nested
    inner class deleteStage {
        var 테코대학교_축제_공연_식별자: Long = 0

        @BeforeEach
        fun setUp() {
            테코대학교_축제_공연_식별자 = stageCreateService.createStage(
                StageCreateCommand(
                    festivalId = 테코대학교_축제_식별자,
                    startTime = festivalStartDate.atTime(18, 0),
                    ticketOpenTime = festivalStartDate.minusWeeks(1).atTime(18, 0),
                    artistIds = listOf(에픽하이_식별자, 소녀시대_식별자, 뉴진스_식별자)
                )
            )
        }

        @Test
        fun 공연을_삭제하면_StageQueryInfo가_삭제된다() {
            // when
            stageDeleteService.deleteStage(테코대학교_축제_공연_식별자)

            // then
            stageQueryInfoRepository.findByStageId(테코대학교_축제_공연_식별자) shouldBe null
        }

        @Test
        fun 공연을_삭제하면_FestivalQueryInfo가_갱신된다() {
            // when
            stageDeleteService.deleteStage(테코대학교_축제_공연_식별자)

            // then
            val festivalQueryInfo = festivalQueryInfoRepository.findByFestivalId(테코대학교_축제_식별자)!!
            festivalQueryInfo.artistInfo shouldBe "[]"
        }
    }
}
