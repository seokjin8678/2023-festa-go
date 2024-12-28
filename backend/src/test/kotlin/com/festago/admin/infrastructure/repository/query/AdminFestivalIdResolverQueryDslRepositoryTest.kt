package com.festago.admin.infrastructure.repository.query

import com.festago.festival.domain.FestivalRepository
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class AdminFestivalIdResolverQueryDslRepositoryTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminFestivalIdResolverQueryDslRepository: AdminFestivalIdResolverQueryDslRepository

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    lateinit var 테코대학교: School

    val _6월_12일: LocalDate = LocalDate.parse("2077-06-12")
    val _6월_13일: LocalDate = LocalDate.parse("2077-06-13")
    val _6월_14일: LocalDate = LocalDate.parse("2077-06-14")
    val _6월_15일: LocalDate = LocalDate.parse("2077-06-15")

    @BeforeEach
    fun setUp() {
        테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())
    }

    @Nested
    inner class findFestivalIdsByWithinDates {

        @Test
        fun 축제의_시작일에_포함되는_축제의_식별자_목록을_반환한다() {
            // given
            val _6월_12일_축제 = festivalRepository.save(
                FestivalFixture.builder().startDate(_6월_12일).school(테코대학교).build()
            )
            val _6월_13일_축제 = festivalRepository.save(
                FestivalFixture.builder().startDate(_6월_13일).school(테코대학교).build()
            )
            val _6월_14일_축제 = festivalRepository.save(
                FestivalFixture.builder().startDate(_6월_14일).school(테코대학교).build()
            )
            val _6월_15일_축제 = festivalRepository.save(
                FestivalFixture.builder().startDate(_6월_15일).school(테코대학교).build()
            )

            // when
            val actual = adminFestivalIdResolverQueryDslRepository.findFestivalIdsByStartDatePeriod(_6월_13일, _6월_14일)

            // then
            actual shouldContainExactlyInAnyOrder listOf(_6월_13일_축제.id, _6월_14일_축제.id)
        }
    }
}
