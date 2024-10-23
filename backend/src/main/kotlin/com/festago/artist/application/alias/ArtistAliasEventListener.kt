package com.festago.artist.application.alias

import com.festago.artist.dto.event.ArtistCreatedEvent
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ArtistAliasEventListener(
    private val artistAliasService: ArtistAliasService,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun createArtistAlias(event: ArtistCreatedEvent) {
        val artist = event.artist
        artistAliasService.createArtistAlias(
            artistId = artist.identifier,
            alias = artist.name
        )
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun updateArtistAlias(event: ArtistUpdatedEvent) {
        val artist = event.artist
        artistAliasService.updateArtistAlias(
            artistId = artist.identifier,
            alias = artist.name
        )
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun deleteArtistAlias(event: ArtistDeletedEvent) {
        val artistId = event.artistId
        artistAliasService.deleteArtistAlias(
            artistId = artistId,
        )
    }
}
