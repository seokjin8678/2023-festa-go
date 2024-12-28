package com.festago.school.application.integration

import com.festago.school.application.v1.SchoolSearchV1QueryService
import com.festago.school.domain.School
import com.festago.school.domain.SchoolRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.SchoolFixture
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class SchoolSearchV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var schoolSearchV1QueryService: SchoolSearchV1QueryService

    @Autowired
    lateinit var schoolRepository: SchoolRepository

    lateinit var 테코대학교: School

    lateinit var 테코여자대학교: School

    lateinit var 우테대학교: School

    @BeforeEach
    fun setUp() {
        테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").domain("teco.ac.kr").build())
        테코여자대학교 = schoolRepository.save(SchoolFixture.builder().name("테코여자대학교").domain("tecowoman.ac.kr").build())
        우테대학교 = schoolRepository.save(SchoolFixture.builder().name("우테대학교").domain("woote.ac.kr").build())
    }

    @Test
    fun 우테대학교를_검색하면_우테대학교가_검색되어야_한다() {
        // given
        val keyword = "우테대학교"

        // when
        val response = schoolSearchV1QueryService.searchSchools(keyword)

        // then
        response.map { it.id } shouldContainExactly listOf(우테대학교.id)
    }

    @Test
    fun 테코를_검색하면_테코대학교와_테코여자대학교가_검색되어야_한다() {
        // given
        val keyword = "테코"

        // when
        val response = schoolSearchV1QueryService.searchSchools(keyword)

        // then
        response.map { it.id } shouldContainExactly listOf(테코대학교.id, 테코여자대학교.id)
    }

    @Test
    fun 학교의_이름에_포함되지_않으면_빈_리스트가_반환된다() {
        // given
        val keyword = "글렌"

        // when
        val response = schoolSearchV1QueryService.searchSchools(keyword)

        // then
        response shouldHaveSize 0
    }
}
