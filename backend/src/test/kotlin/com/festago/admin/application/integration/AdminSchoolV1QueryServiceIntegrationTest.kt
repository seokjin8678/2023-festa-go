package com.festago.admin.application.integration

import com.festago.admin.application.AdminSchoolV1QueryService
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.querydsl.SearchCondition
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRegion
import com.festago.school.domain.SchoolRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.SchoolFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

internal class AdminSchoolV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminSchoolV1QueryService: AdminSchoolV1QueryService

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    lateinit var 테코대학교: School
    lateinit var 우테대학교: School
    lateinit var 글렌대학교: School

    @BeforeEach
    fun setUp() {
        테코대학교 = schoolRepository.save(
            SchoolFixture.builder()
                .name("테코대학교")
                .domain("teco.ac.kr")
                .region(SchoolRegion.서울)
                .build()
        )
        우테대학교 = schoolRepository.save(
            SchoolFixture.builder()
                .name("우테대학교")
                .domain("wote.ac.kr")
                .region(SchoolRegion.서울)
                .build()
        )
        글렌대학교 = schoolRepository.save(
            SchoolFixture.builder()
                .name("글렌대학교")
                .domain("glen.ac.kr")
                .region(SchoolRegion.대구)
                .build()
        )
    }

    @Nested
    inner class findAll {

        @Test
        fun 정렬이_되어야_한다() {
            // given
            val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
            val searchCondition = SearchCondition("", "", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(글렌대학교.name, 우테대학교.name, 테코대학교.name)
        }

        @Test
        fun 식별자로_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("id", 글렌대학교.id.toString(), pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(글렌대학교.name)
        }

        @Test
        fun 지역으로_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("region", "서울", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactlyInAnyOrder listOf(우테대학교.name, 테코대학교.name)
        }

        @Test
        fun 도메인이_포함된_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("domain", "wote", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(우테대학교.name)
        }

        @Test
        fun 이름이_포함된_검색이_되어야_한다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("name", "글렌", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content.map { it.name } shouldContainExactly listOf(글렌대학교.name)
        }

        @Test
        fun 검색_필터가_비어있으면_필터링이_적용되지_않는다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("", "글렌", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content shouldHaveSize 3
        }

        @Test
        fun 검색어가_비어있으면_필터링이_적용되지_않는다() {
            // given
            val pageable = Pageable.ofSize(10)
            val searchCondition = SearchCondition("id", "", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.content shouldHaveSize 3
        }

        @Test
        fun 페이지네이션이_적용_되어야_한다() {
            // given
            val pageable = PageRequest.of(0, 2)
            val searchCondition = SearchCondition("", "", pageable)

            // when
            val response = adminSchoolV1QueryService.findAll(searchCondition)

            // then
            response.size shouldBe 2
            response.totalPages shouldBe 2
            response.totalElements shouldBe 3
        }
    }

    @Nested
    inner class findById {

        @Test
        fun 식별자로_조회가_되어야_한다() {
            // given
            val 테코대학교_식별자 = 테코대학교.identifier

            // when
            val response = adminSchoolV1QueryService.findById(테코대학교_식별자)

            // then
            response.name shouldBe "테코대학교"
        }

        @Test
        fun 식별자로_찾을_수_없으면_예외가_발생한다() {
            // given
            val invalidId = 4885L

            // when
            val ex = shouldThrow<NotFoundException> {
                adminSchoolV1QueryService.findById(invalidId)
            }

            // then
            ex shouldHaveMessage ErrorCode.SCHOOL_NOT_FOUND.message
        }
    }
}
