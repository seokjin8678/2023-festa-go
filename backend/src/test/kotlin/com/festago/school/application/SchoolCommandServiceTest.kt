package com.festago.school.application

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRegion
import com.festago.school.domain.SchoolRepository
import com.festago.school.domain.getOrThrow
import com.festago.school.dto.command.SchoolCreateCommand
import com.festago.school.dto.command.SchoolUpdateCommand
import com.festago.school.infrastructure.repository.MemorySchoolRepository
import com.festago.support.fixture.SchoolFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SchoolCommandServiceTest {

    lateinit var schoolCommandService: SchoolCommandService

    lateinit var schoolRepository: SchoolRepository

    @BeforeEach
    fun setUp() {
        schoolRepository = MemorySchoolRepository()
        schoolCommandService = SchoolCommandService(schoolRepository, mockk(relaxed = true))
    }

    @Nested
    inner class createSchool {

        var command = SchoolCreateCommand(
            name = "테코대학교",
            domain = "teco.ac.kr",
            region = SchoolRegion.서울
        )

        @Test
        fun 같은_도메인의_학교가_저장되어_있어도_예외가_발생하지_않는다() {
            // given
            schoolRepository.save(SchoolFixture.builder().domain("teco.ac.kr").build())

            // when
            val schoolId = schoolCommandService.createSchool(command)

            // then
            schoolRepository.getOrThrow(schoolId).domain shouldBe "teco.ac.kr"
        }

        @Test
        fun 같은_이름의_학교가_저장되어_있으면_예외가_발생한다() {
            // given
            schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())

            // when
            val ex = shouldThrow<BadRequestException> {
                schoolCommandService.createSchool(command)
            }

            // then
            ex shouldHaveMessage ErrorCode.DUPLICATE_SCHOOL_NAME.message
        }

        @Test
        fun 예외가_발생하지_않으면_학교가_저장된다() {
            // when
            val schoolId = schoolCommandService.createSchool(command)

            // then
            schoolRepository.findById(schoolId) shouldNotBe null
        }
    }

    @Nested
    inner class updateSchool {
        lateinit var school: School
        val command = SchoolUpdateCommand(
            name = "테코대학교",
            domain = "teco.ac.kr",
            region = SchoolRegion.서울,
            logoUrl = "https://image.com/newLogo.png",
            backgroundImageUrl = "https://image.com/newBackgroundImage.png",
        )

        @BeforeEach
        fun setUp() {
            school = schoolRepository.save(
                SchoolFixture.builder()
                    .name("우테대학교")
                    .domain("wote.ac.kr")
                    .region(SchoolRegion.대구)
                    .logoUrl("https://image.com/logo.png")
                    .backgroundImageUrl("https://image.com/backgroundImage.png")
                    .build()
            )
        }

        @Test
        fun 식별자에_대한_학교를_찾을수_없으면_예외가_발생한다() {
            // given
            val schoolId = 4885L

            // when
            val ex = shouldThrow<NotFoundException> {
                schoolCommandService.updateSchool(schoolId, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.SCHOOL_NOT_FOUND.message
        }

        @Test
        fun 같은_도메인의_학교가_저장되어_있어도_예외가_발생하지_않는다() {
            // given
            val schoolId = school.identifier
            schoolRepository.save(SchoolFixture.builder().domain("teco.ac.kr").build())

            // when
            schoolCommandService.updateSchool(schoolId, command)

            //  then
            schoolRepository.getOrThrow(schoolId).domain shouldBe "teco.ac.kr"
        }

        @Test
        fun 같은_이름의_학교가_저장되어_있으면_예외가_발생한다() {
            // given
            val schoolId = school.identifier
            schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())

            // when
            val ex = shouldThrow<BadRequestException> {
                schoolCommandService.updateSchool(schoolId, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.DUPLICATE_SCHOOL_NAME.message
        }

        @Test
        fun 수정할_이름이_수정할_학교의_이름과_같으면_이름은_수정되지_않는다() {
            // given
            val schoolId = school.identifier
            val command = SchoolUpdateCommand(
                name = school.name,
                domain = "teco.ac.kr",
                region = SchoolRegion.서울,
            )

            // when
            schoolCommandService.updateSchool(schoolId, command)

            // then
            val updatedSchool = schoolRepository.getOrThrow(schoolId)
            updatedSchool.name shouldBe school.name
        }

        @Test
        fun 수정할_도메인이_수정할_학교의_도메인과_같으면_도메인은_수정되지_않는다() {
            // given
            val schoolId = school.identifier
            val command = SchoolUpdateCommand(
                name = "테코대학교",
                domain = school.domain,
                region = SchoolRegion.서울
            )

            // when
            schoolCommandService.updateSchool(schoolId, command)

            // then
            val updatedSchool = schoolRepository.getOrThrow(schoolId)
            updatedSchool.domain shouldBe school.domain
        }

        @Test
        fun 예외가_발생하지_않으면_학교가_수정된다() {
            // given
            val schoolId = school.identifier

            // when
            schoolCommandService.updateSchool(schoolId, command)

            // then
            val updatedSchool = schoolRepository.getOrThrow(schoolId)
            updatedSchool.name shouldBe "테코대학교"
            updatedSchool.domain shouldBe "teco.ac.kr"
            updatedSchool.region shouldBe SchoolRegion.서울
            updatedSchool.logoUrl shouldBe "https://image.com/newLogo.png"
            updatedSchool.backgroundUrl shouldBe "https://image.com/newBackgroundImage.png"
        }
    }
}
