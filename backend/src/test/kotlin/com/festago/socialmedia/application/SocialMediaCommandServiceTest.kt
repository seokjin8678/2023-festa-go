package com.festago.socialmedia.application

import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.MemoryArtistRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.repository.MemorySchoolRepository
import com.festago.school.repository.SchoolRepository
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMediaType
import com.festago.socialmedia.dto.command.SocialMediaCreateCommand
import com.festago.socialmedia.dto.command.SocialMediaUpdateCommand
import com.festago.socialmedia.repository.MemorySocialMediaRepository
import com.festago.socialmedia.repository.SocialMediaRepository
import com.festago.socialmedia.repository.getOrThrow
import com.festago.support.fixture.SchoolFixture
import com.festago.support.fixture.SocialMediaFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SocialMediaCommandServiceTest {

    lateinit var socialMediaCommandService: SocialMediaCommandService

    lateinit var socialMediaRepository: SocialMediaRepository

    lateinit var schoolRepository: SchoolRepository

    lateinit var artistRepository: ArtistRepository

    @BeforeEach
    fun setUp() {
        socialMediaRepository = MemorySocialMediaRepository()
        schoolRepository = MemorySchoolRepository()
        artistRepository = MemoryArtistRepository()
        socialMediaCommandService = SocialMediaCommandService(
            socialMediaRepository,
            schoolRepository,
            artistRepository
        )
    }

    @Nested
    inner class createSocialMedia {

        @Test
        fun 중복된_소셜미디어가_있으면_예외() {
            // given
            val 테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())
            val socialMedia = socialMediaRepository.save(
                SocialMediaFixture.builder()
                    .ownerId(테코대학교.id)
                    .ownerType(OwnerType.SCHOOL)
                    .mediaType(SocialMediaType.INSTAGRAM)
                    .build()
            )

            // when
            val command = SocialMediaCreateCommand(
                ownerId = socialMedia.ownerId,
                ownerType = socialMedia.ownerType,
                socialMediaType = socialMedia.mediaType,
                name = "테코대학교 인스타그램",
                logoUrl = "https://image.com/logo.png",
                url = "https://instagram.com/tecodaehak",
            )
            val ex = shouldThrow<BadRequestException> {
                socialMediaCommandService.createSocialMedia(command)
            }

            // then
            ex shouldHaveMessage ErrorCode.DUPLICATE_SOCIAL_MEDIA.message
        }

        @Test
        fun 추가하려는_소셜미디어의_owner가_존재하지_않으면_예외() {
            // when
            val command = SocialMediaCreateCommand(
                ownerId = 4885L,
                ownerType = OwnerType.SCHOOL,
                socialMediaType = SocialMediaType.INSTAGRAM,
                name = "테코대학교 인스타그램",
                logoUrl = "https://image.com/logo.png",
                url = "https://instagram.com/tecodaehak",
            )
            val ex = shouldThrow<NotFoundException> {
                socialMediaCommandService.createSocialMedia(command)
            }

            // then
            ex shouldHaveMessage ErrorCode.SCHOOL_NOT_FOUND.message
        }

        @Test
        fun 성공하면_소셜미디어가_저장된다() {
            // given
            val 테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())

            // when
            val command = SocialMediaCreateCommand(
                ownerId = 테코대학교.identifier,
                ownerType = OwnerType.SCHOOL,
                socialMediaType = SocialMediaType.INSTAGRAM,
                name = "테코대학교 인스타그램",
                logoUrl = "https://image.com/logo.png",
                url = "https://instagram.com/tecodaehak",
            )

            val socialMediaId = socialMediaCommandService.createSocialMedia(command)

            // then
            socialMediaRepository.findById(socialMediaId) shouldNotBe null
        }
    }

    @Nested
    inner class updateSocialMedia {

        @Test
        fun 소셜미디어의_식별자에_대한_소셜미디어가_존재하지_않으면_예외() {
            // when
            val command = SocialMediaUpdateCommand(
                name = "테코대학교 인스타그램",
                url = "https://instagram.com/tecodaehak",
                logoUrl = "https://image.com/logo.png"
            )
            val ex = shouldThrow<NotFoundException> {
                socialMediaCommandService.updateSocialMedia(4885L, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.SOCIAL_MEDIA_NOT_FOUND.message
        }

        @Test
        fun 성공하면_소셜미디어의_정보가_변경된다() {
            // given
            val 테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())
            val socialMedia = socialMediaRepository.save(
                SocialMediaFixture.builder()
                    .ownerId(테코대학교.id)
                    .ownerType(OwnerType.SCHOOL)
                    .mediaType(SocialMediaType.INSTAGRAM)
                    .build()
            )

            // when
            val command = SocialMediaUpdateCommand(
                name = "테코대학교 인스타그램",
                url = "https://instagram.com/tecodaehak",
                logoUrl = "https://image.com/logo.png"
            )
            socialMediaCommandService.updateSocialMedia(socialMedia.identifier, command)

            // then
            val actual = socialMediaRepository.getOrThrow(socialMedia.identifier)
            actual.name shouldBe command.name
            actual.url shouldBe command.url
            actual.logoUrl shouldBe command.logoUrl
        }
    }

    @Nested
    inner class deleteSocialMedia {

        @Test
        fun 삭제하려는_소셜미디어가_존재하지_않아도_예외가_발생하지_않는다() {
            // when & then
            shouldNotThrowAny {
                socialMediaCommandService.deleteSocialMedia(4885L)
            }
        }

        @Test
        fun 소셜미디어의_식별자로_삭제할_수_있다() {
            // given
            val 테코대학교 = schoolRepository.save(SchoolFixture.builder().name("테코대학교").build())

            val socialMedia = socialMediaRepository.save(
                SocialMediaFixture.builder()
                    .ownerId(테코대학교.id)
                    .ownerType(OwnerType.SCHOOL)
                    .mediaType(SocialMediaType.INSTAGRAM)
                    .build()
            )

            // when
            socialMediaCommandService.deleteSocialMedia(socialMedia.identifier)

            // then
            socialMediaRepository.findById(socialMedia.identifier) shouldBe null
        }
    }
}
