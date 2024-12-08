package com.festago.artist.application

import com.festago.artist.domain.Artist
import com.festago.artist.dto.command.ArtistCreateCommand
import com.festago.artist.dto.command.ArtistUpdateCommand
import com.festago.artist.dto.event.ArtistCreatedEvent
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import com.festago.artist.repository.ArtistRepository
import com.festago.artist.repository.getOrThrow
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArtistCommandService(
    private val artistRepository: ArtistRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun save(command: ArtistCreateCommand): Long {
        validateSave(command)
        val artist = artistRepository.save(
            Artist(
                name = command.name,
                profileImage = command.profileImageUrl,
                backgroundImageUrl = command.backgroundImageUrl
            )
        )
        eventPublisher.publishEvent(ArtistCreatedEvent(artist))
        return artist.identifier
    }

    private fun validateSave(command: ArtistCreateCommand) {
        if (artistRepository.existsByName(command.name)) {
            throw BadRequestException(ErrorCode.DUPLICATE_ARTIST_NAME)
        }
    }

    fun update(command: ArtistUpdateCommand, artistId: Long) {
        val artist: Artist = artistRepository.getOrThrow(artistId)
        val oldArtist = artist.copy()
        artist.update(
            name = command.name,
            profileImage = command.profileImageUrl,
            backgroundImageUrl = command.backgroundImageUrl
        )
        eventPublisher.publishEvent(
            ArtistUpdatedEvent(
                artist = artist,
                oldArtist = oldArtist
            )
        )
    }

    fun delete(artistId: Long) {
        artistRepository.deleteById(artistId)
        eventPublisher.publishEvent(ArtistDeletedEvent(artistId))
    }
}
