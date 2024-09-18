package com.festago.festival.application.integration.query

import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.application.query.FestivalDetailV1QueryService
import com.festago.festival.repository.FestivalRepository
import com.festago.school.domain.School
import com.festago.school.repository.SchoolRepository
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.socialmedia.repository.SocialMediaRepository
import com.festago.stage.repository.StageArtistRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.SocialMediaFixture
import com.festago.support.fixture.StageArtistFixture
import com.festago.support.fixture.StageFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDate

class FestivalDetailV1QueryServiceIntegrationTest(
    val festivalDetailV1QueryService: FestivalDetailV1QueryService,
    val socialMediaRepository: SocialMediaRepository,
    val schoolRepository: SchoolRepository,
    val festivalRepository: FestivalRepository,
    val stageRepository: StageRepository,
    val stageArtistRepository: StageArtistRepository,
    val artistRepository: ArtistRepository,
) : IntegrationDescribeSpec({

    fun createSchool(name: String): School {
        val school = SchoolFixture.builder()
            .name(name)
            .build()
        return schoolRepository.save(school)
    }

    fun createArtist(name: String): Artist {
        val artist = ArtistFixture.builder()
            .name(name)
            .build()
        return artistRepository.save(artist)
    }

    /**
     * 테코대학교 축제는 공연이 있는 3일 기간의 축제와 공연이 없는 당일 축제가 있다.
     *
     * 테코대학교는 소셜미디어에 인스타그램과 페이스북이 등록되어 있다.
     *
     * 우테대학교 축제는 공연이 있는 당일 축제가 있다.
     *
     * 우테대학교에는 소셜미디어가 등록되어 있지 않다.
     */
    val now = LocalDate.parse("2077-06-30")
    val 테코대학교 = createSchool(
        name = "테코대학교",
    )
    val 우테대학교 = createSchool(
        name = "우테대학교",
    )

    val 테코대학교_축제 = festivalRepository.save(
        FestivalFixture.builder()
            .startDate(now)
            .endDate(now.plusDays(2))
            .school(테코대학교)
            .build()
    )
    val 테코대학교_공연_없는_축제 = festivalRepository.save(
        FestivalFixture.builder()
            .startDate(now)
            .endDate(now)
            .school(테코대학교)
            .build()
    )
    val 우테대학교_축제 = festivalRepository.save(
        FestivalFixture.builder()
            .startDate(now)
            .endDate(now)
            .school(우테대학교)
            .build()
    )

    val 아티스트A = createArtist(name = "아티스트A")

    val 테코대학교_축제_1일차_공연 = stageRepository.save(
        StageFixture.builder()
            .festival(테코대학교_축제)
            .startTime(now.atTime(18, 0))
            .build()
    )
    val 테코대학교_축제_2일차_공연 = stageRepository.save(
        StageFixture.builder()
            .festival(테코대학교_축제)
            .startTime(now.plusDays(1).atTime(18, 0))
            .build()
    )
    val 테코대학교_축제_3일차_공연 = stageRepository.save(
        StageFixture.builder()
            .festival(테코대학교_축제)
            .startTime(now.plusDays(2).atTime(18, 0))
            .build()
    )
    val 우테대학교_축제_당일_공연 = stageRepository.save(
        StageFixture.builder()
            .festival(우테대학교_축제)
            .startTime(now.atTime(18, 0))
            .build()
    )

    stageArtistRepository.save(StageArtistFixture.builder(테코대학교_축제_1일차_공연.id, 아티스트A.id).build())
    stageArtistRepository.save(StageArtistFixture.builder(테코대학교_축제_2일차_공연.id, 아티스트A.id).build())
    stageArtistRepository.save(StageArtistFixture.builder(테코대학교_축제_3일차_공연.id, 아티스트A.id).build())
    stageArtistRepository.save(StageArtistFixture.builder(우테대학교_축제_당일_공연.id, 아티스트A.id).build())

    socialMediaRepository.save(
        SocialMediaFixture.builder()
            .ownerId(테코대학교.id)
            .ownerType(OwnerType.SCHOOL)
            .mediaType(SocialMediaType.INSTAGRAM)
            .name("총학생회 인스타그램")
            .logoUrl("https://logo.com/instagram.png")
            .url("https://instagram.com/테코대학교_총학생회")
            .build()
    )
    socialMediaRepository.save(
        SocialMediaFixture.builder()
            .ownerId(테코대학교.id)
            .ownerType(OwnerType.SCHOOL)
            .mediaType(SocialMediaType.FACEBOOK)
            .name("총학생회 페이스북")
            .logoUrl("https://logo.com/instagram.png")
            .url("https://facebook.com/테코대학교_총학생회")
            .build()
    )

    describe("축제 상세 조회") {
        context("테코대학교 축제 조회") {
            val actual = festivalDetailV1QueryService.findFestivalDetail(테코대학교_축제.id!!)

            it("해당 축제의 정보가 반환된다") {
                actual.id shouldBe 테코대학교_축제.id
                actual.startDate shouldBe 테코대학교_축제.startDate
                actual.endDate shouldBe 테코대학교_축제.endDate
                actual.school.name shouldBe 테코대학교_축제.school.name
                actual.stages shouldHaveSize 3
            }

            it("공연 목록은 공연의 시작 시간 기준으로 오름차순 정렬된다") {
                actual.stages.map { it.id } shouldContainExactly listOf(
                    테코대학교_축제_1일차_공연.id,
                    테코대학교_축제_2일차_공연.id,
                    테코대학교_축제_3일차_공연.id
                )
            }

            it("소셜미디어는 소셜미디어 이름 기준으로 오름차순 정렬된다") {
                actual.socialMedias.map { it.name } shouldContainExactly listOf("총학생회 인스타그램", "총학생회 페이스북")
            }
        }

        context("테코대학교 공연 없는 축제 조회") {
            val actual = festivalDetailV1QueryService.findFestivalDetail(테코대학교_공연_없는_축제.id!!)

            it("축제에 공연이 없으면 비어있는 공연이 반환된다") {
                actual.id shouldBe 테코대학교_공연_없는_축제.id
                actual.stages shouldHaveSize 0
            }
        }

        context("우테대학교 축제 조회") {
            val actual = festivalDetailV1QueryService.findFestivalDetail(우테대학교_축제.id!!)

            it("축제를 개최하는 학교에 소셜미디어가 없으면 비어있는 소셜미디어가 반환된다") {
                actual.id shouldBe 우테대학교_축제.id
                actual.socialMedias shouldHaveSize 0
            }
        }

        context("존재하지 않는 축제 조회") {
            it("존재하지 않는 축제를 조회하면 예외가 발생한다") {
                val ex = shouldThrow<NotFoundException> {
                    festivalDetailV1QueryService.findFestivalDetail(4885)
                }
                ex shouldHaveMessage ErrorCode.FESTIVAL_NOT_FOUND.message
            }
        }
    }
})