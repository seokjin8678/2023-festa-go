package com.festago.stage.infrastructure

import com.festago.artist.domain.Artist
import com.festago.artist.domain.ArtistRepository
import com.festago.festival.domain.FestivalRepository
import com.festago.school.domain.SchoolRepository
import com.festago.stage.domain.Stage
import com.festago.stage.domain.StageArtistRepository
import com.festago.stage.domain.StageRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.StageArtistFixture
import com.festago.support.fixture.StageFixture
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class FestivalIdStageArtistsQueryDslResolverTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var stageRepository: StageRepository

    @Autowired
    lateinit var stageArtistRepository: StageArtistRepository

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var festivalIdStageArtistsResolver: FestivalIdStageArtistsQueryDslResolver

    var festivalId: Long = 0

    lateinit var 테코대학교_두번째_공연: Stage

    lateinit var 뉴진스: Artist
    lateinit var 에픽하이: Artist
    lateinit var 아이유: Artist

    @BeforeEach
    fun setUp() {
        val 테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())
        뉴진스 = artistRepository.save(ArtistFixture.builder().name("뉴진스").build())
        에픽하이 = artistRepository.save(ArtistFixture.builder().name("에픽하이").build())
        아이유 = artistRepository.save(ArtistFixture.builder().name("아이유").build())
        val 테코대학교_축제 = festivalRepository.save(FestivalFixture.builder().school(테코대학교).build())
        val 테코대학교_첫번째_공연 = stageRepository.save(StageFixture.builder().festival(테코대학교_축제).build())
        테코대학교_두번째_공연 = stageRepository.save(StageFixture.builder().festival(테코대학교_축제).build())
        stageArtistRepository.save(StageArtistFixture.builder(테코대학교_첫번째_공연.id, 뉴진스.id).build())
        stageArtistRepository.save(StageArtistFixture.builder(테코대학교_첫번째_공연.id, 아이유.id).build())
        stageArtistRepository.save(StageArtistFixture.builder(테코대학교_두번째_공연.id, 에픽하이.id).build())
        festivalId = 테코대학교_축제.identifier
    }

    @Test
    fun 축제의_식별자로_축제의_공연에_참여하는_아티스트를_모두_조회한다() {
        // when
        val actual = festivalIdStageArtistsResolver.resolve(festivalId)

        // then
        actual.map { it.name } shouldContainExactly listOf(뉴진스.name, 에픽하이.name, 아이유.name)
    }

    @Test
    fun 중복된_아티스트가_있으면_중복된_아티스트는_포함되지_않는다() {
        // given
        stageArtistRepository.save(StageArtistFixture.builder(테코대학교_두번째_공연.id, 아이유.id).build())

        // when
        val actual = festivalIdStageArtistsResolver.resolve(festivalId)

        // then
        actual.map { it.name } shouldContainExactly listOf(뉴진스.name, 에픽하이.name, 아이유.name)
    }
}
