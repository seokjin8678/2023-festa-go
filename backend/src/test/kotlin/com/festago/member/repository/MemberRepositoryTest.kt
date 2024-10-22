package com.festago.member.repository

import com.festago.auth.domain.SocialType
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.MemberFixture
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.simple.JdbcClient

class MemberRepositoryTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var jdbcClient: JdbcClient

    @Test
    fun 회원_삭제() {
        // given
        val member = MemberFixture.builder().build()
        val expected = memberRepository.save(member)

        // when
        memberRepository.delete(expected)

        val actual = jdbcClient.sql("SELECT * FROM member WHERE id = :id")
            .param("id", expected.id)
            .query { rs, _ ->
                mapOf(
                    "id" to rs.getLong("id"),
                    "socialId" to rs.getString("social_id"),
                    "socialType" to SocialType.valueOf(rs.getString("social_type")),
                    "nickname" to rs.getString("nickname"),
                    "profileImage" to rs.getString("profile_image_url"),
                    "deletedAt" to rs.getTimestamp("deleted_at").toLocalDateTime()
                )
            }
            .single()

        // then
        actual["id"] shouldBe expected.id
        actual["nickname"] shouldBe "탈퇴한 회원"
        actual["socialId"] shouldBe null
        actual["socialType"] shouldNotBe null
        actual["profileImage"] shouldBe ""
        actual["deletedAt"] shouldNotBe null
    }
}
