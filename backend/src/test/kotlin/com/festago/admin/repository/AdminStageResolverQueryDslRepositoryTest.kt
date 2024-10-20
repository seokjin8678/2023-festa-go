package com.festago.admin.repository

import com.festago.festival.repository.FestivalRepository
import com.festago.school.domain.School
import com.festago.school.repository.SchoolRepository
import com.festago.stage.repository.StageRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.StageFixture
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class AdminStageResolverQueryDslRepositoryTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminStageResolverQueryDslRepository: AdminStageResolverQueryDslRepository

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var stageRepository: StageRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    lateinit var 테코대학교: School

    @BeforeEach
    fun setUp() {
        테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())
    }

    @Nested
    inner class findStageIdsByFestivalId {

        @Test
        fun 축제_식별자로_공연을_모두_조회한다() {
            // given
            val festival = festivalRepository.save(FestivalFixture.builder().school(테코대학교).build())
            val expect = sequenceOf(1..3)
                .map { stageRepository.save(StageFixture.builder().festival(festival).build()) }
                .map { it.id }
                .toList()

            // when
            val actual = adminStageResolverQueryDslRepository.findStageByFestivalId(festival.identifier)

            // then
            actual.map { it.id } shouldContainExactlyInAnyOrder expect
        }
    }

    @Nested
    inner class findStageIdsByFestivalIdIn {

        @Test
        fun 축제_식별자_목록으로_공연을_모두_조회한다() {
            // given
            val festivals = sequenceOf(1..2)
                .map { festivalRepository.save(FestivalFixture.builder().school(테코대학교).build()) }
                .toList()
            val expect = festivals.asSequence()
                .flatMap { festival ->
                    sequenceOf(1..3)
                        .map { stageRepository.save(StageFixture.builder().festival(festival).build()) }
                        .map { it.id }
                }
                .toList()

            // when
            val festivalIds = festivals.map { it.identifier }
            val actual = adminStageResolverQueryDslRepository.findStageByFestivalIdIn(festivalIds)

            // then
            actual.map { it.id } shouldContainExactlyInAnyOrder expect
        }
    }
}
