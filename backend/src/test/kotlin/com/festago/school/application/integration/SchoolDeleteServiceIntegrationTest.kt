package com.festago.school.application.integration

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.repository.FestivalRepository
import com.festago.school.application.SchoolDeleteService
import com.festago.school.domain.School
import com.festago.school.repository.SchoolRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.FestivalFixture
import com.festago.support.fixture.SchoolFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class SchoolDeleteServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var schoolDeleteService: SchoolDeleteService

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    @Autowired
    lateinit var festivalRepository: FestivalRepository

    @Nested
    inner class deleteSchool {
        lateinit var school: School

        @BeforeEach
        fun setUp() {
            school = schoolRepository.save(SchoolFixture.builder().build())
        }

        @Test
        fun 학교에_등록된_축제가_있으면_삭제에_실패한다() {
            // given
            val schoolId = school.identifier
            festivalRepository.save(FestivalFixture.builder().school(school).build())

            // when
            val ex = shouldThrow<BadRequestException> {
                schoolDeleteService.deleteSchool(schoolId)
            }

            // then
            ex shouldHaveMessage ErrorCode.SCHOOL_DELETE_CONSTRAINT_EXISTS_FESTIVAL.message
        }

        @Test
        fun Validator의_검증이_정상이면_학교가_삭제된다() {
            // given
            val schoolId = school.identifier

            // when
            schoolDeleteService.deleteSchool(schoolId)

            // then
            schoolRepository.findById(schoolId) shouldBe null
        }
    }
}
