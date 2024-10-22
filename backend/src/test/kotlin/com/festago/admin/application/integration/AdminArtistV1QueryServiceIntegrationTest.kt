package com.festago.admin.application.integration

import com.festago.admin.application.AdminArtistV1QueryService
import com.festago.artist.domain.Artist
import com.festago.artist.repository.ArtistRepository
import com.festago.common.querydsl.SearchCondition
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.ArtistFixture
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

internal class AdminArtistV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminArtistV1QueryService: AdminArtistV1QueryService

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Test
    fun 아티스트를_단건_조회한다() {
        // given
        val expected = artistRepository.save(ArtistFixture.builder().build())

        // when
        val actual = adminArtistV1QueryService.findById(expected.identifier)

        // then
        actual.id shouldBe expected.id
        actual.name shouldBe expected.name
        actual.profileImageUrl shouldBe expected.profileImage
        actual.backgroundImageUrl shouldBe expected.backgroundImageUrl
    }

    @Nested
    internal inner class findAll {

        lateinit var 벤: Artist
        lateinit var 베토벤: Artist
        lateinit var 아이유: Artist
        lateinit var 에픽하이: Artist
        lateinit var 소녀시대: Artist

        @BeforeEach
        fun setUp() {
            벤 = artistRepository.save(
                ArtistFixture.builder()
                    .name("벤")
                    .build()
            )
            베토벤 = artistRepository.save(
                ArtistFixture.builder()
                    .name("베토벤")
                    .build()
            )
            아이유 = artistRepository.save(
                ArtistFixture.builder()
                    .name("아이유")
                    .build()
            )
            에픽하이 = artistRepository.save(
                ArtistFixture.builder()
                    .name("에픽하이")
                    .build()
            )
            소녀시대 = artistRepository.save(
                ArtistFixture.builder()
                    .name("소녀시대")
                    .build()
            )
        }

        @Test
        fun 정렬이_되어야_한다() {
            // given
            val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
            val searchCondition = SearchCondition("", "", pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(
                베토벤.name, 벤.name, 소녀시대.name, 아이유.name, 에픽하이.name
            )
        }

        @Test
        fun 식별자로_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("id", 소녀시대.id.toString(), pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)


            // then
            response.content.map { it.name } shouldContainExactly listOf(소녀시대.name)
        }

        @Test
        fun 이름이_포함된_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("name", "에픽", pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(에픽하이.name)
        }

        @Test
        fun 이름으로_검색할때_한_글자이면_동등_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("name", "벤", pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(벤.name)
        }

        @Test
        fun 검색_필터가_비어있으면_필터링이_적용되지_않는다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("", "글렌", pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)

            // then
            response.content shouldHaveSize 5
        }

        @Test
        fun 검색어가_비어있으면_필터링이_적용되지_않는다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("id", "", pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)

            // then
            response.content shouldHaveSize 5
        }

        @Test
        fun 페이지네이션이_적용_되어야_한다() {
            // given
            val pageable: Pageable = PageRequest.of(0, 2)
            val searchCondition = SearchCondition("", "", pageable)

            // when
            val response = adminArtistV1QueryService.findAll(searchCondition)

            // then
            response.size shouldBe 2
            response.totalPages shouldBe 3
            response.totalElements shouldBe 5
        }
    }
}
