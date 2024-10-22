package com.festago.admin.application.integration

import com.festago.admin.application.AdminSocialMediaV1QueryService
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.socialmedia.repository.SocialMediaRepository
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.SocialMediaFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class AdminSocialMediaV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var adminSocialMediaV1QueryService: AdminSocialMediaV1QueryService

    @Autowired
    lateinit var socialMediaRepository: SocialMediaRepository

    @Nested
    inner class findById {

        @Test
        fun 소셜미디어_식별자로_조회할_수_있다() {
            // given
            val 테코대학교_식별자 = 1L
            val 소셜미디어_식별자 = socialMediaRepository.save(
                SocialMediaFixture.builder()
                    .ownerId(테코대학교_식별자)
                    .ownerType(OwnerType.SCHOOL)
                    .name("테코대학교 소셜미디어")
                    .build()
            ).identifier

            // when
            val actual = adminSocialMediaV1QueryService.findById(소셜미디어_식별자)

            // then
            actual.name shouldBe "테코대학교 소셜미디어"
        }

        @Test
        fun 식별자에_대한_소셜미디어가_존재하지_않으면_예외가_발생한다() {
            // when
            val ex = shouldThrow<NotFoundException> {
                adminSocialMediaV1QueryService.findById(4885L)
            }

            // then
            ex shouldHaveMessage ErrorCode.SOCIAL_MEDIA_NOT_FOUND.message
        }
    }

    @Nested
    inner class findByOwnerIdAndOwnerType {

        @Test
        fun ownerId와_ownerType으로_해당하는_소셜미디어를_모두_조회할_수_있다() {
            // given
            val 테코대학교_식별자 = 1L
            val expect = listOf(SocialMediaType.INSTAGRAM, SocialMediaType.X, SocialMediaType.YOUTUBE)
                .map { mediaType ->
                    socialMediaRepository.save(
                        SocialMediaFixture.builder()
                            .ownerId(테코대학교_식별자)
                            .ownerType(OwnerType.SCHOOL)
                            .mediaType(mediaType)
                            .build()
                    )
                }
                .map { it.identifier }

            // when
            val actual = adminSocialMediaV1QueryService.findByOwnerIdAndOwnerType(테코대학교_식별자, OwnerType.SCHOOL)

            // then
            actual.map { it.id } shouldContainExactlyInAnyOrder expect
        }
    }
}
