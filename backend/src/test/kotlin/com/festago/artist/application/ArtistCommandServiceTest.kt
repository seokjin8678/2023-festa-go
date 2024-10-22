package com.festago.artist.application

import com.festago.artist.dto.command.ArtistCreateCommand
import com.festago.artist.dto.command.ArtistUpdateCommand
import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.MemoryArtistRepository
import com.festago.artist.repository.getOrThrow
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.support.fixture.ArtistFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ArtistCommandServiceTest {

    lateinit var artistCommandService: ArtistCommandService

    lateinit var artistRepository: ArtistRepository

    @BeforeEach
    fun setUp() {
        artistRepository = MemoryArtistRepository()
        artistCommandService = ArtistCommandService(artistRepository, mockk(relaxed = true))
    }

    @Test
    fun 중복된_이름의_아티스트가_저장되면_예외가_발생한다() {
        // given
        artistRepository.save(ArtistFixture.builder().name("윤서연").build())
        val command = ArtistCreateCommand(
            "윤서연", "https://image.com/image.png",
            "https://image.com/image.png"
        )

        // when
        val ex = shouldThrow<BadRequestException> {
            artistCommandService.save(command)
        }

        // then
        ex shouldHaveMessage ErrorCode.DUPLICATE_ARTIST_NAME.message
    }

    @Test
    fun 아티스트_정보를_변경한다() {
        // given
        val artistId = artistRepository.save(ArtistFixture.builder().name("고윤하").build()).identifier
        val command = ArtistUpdateCommand(
            "윤하",
            "https://image.com/image2.png",
            "https://image.com/image2.png"
        )

        // when
        artistCommandService.update(command, artistId)

        // then
        val actual = artistRepository.getOrThrow(artistId)
        actual.name shouldBe command.name
        actual.profileImage shouldBe command.profileImageUrl
        actual.backgroundImageUrl shouldBe command.backgroundImageUrl
    }
}
