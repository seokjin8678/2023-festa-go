package com.festago.upload.application.artist

import com.festago.artist.dto.event.ArtistCreatedEvent
import com.festago.artist.dto.event.ArtistDeletedEvent
import com.festago.artist.dto.event.ArtistUpdatedEvent
import com.festago.upload.application.UploadFileStatusChangeService
import com.festago.upload.domain.FileOwnerType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Async
@Component
class AsyncArtistUploadImagesStatusChangeEventListener(
    private val uploadFileStatusChangeService: UploadFileStatusChangeService,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeAttachedStatusArtistImagesEventHandler(event: ArtistCreatedEvent) {
        val artist = event.artist
        val artistId = artist.identifier
        val imageUris = listOf(artist.profileImage, artist.backgroundImageUrl)
        uploadFileStatusChangeService.changeAttached(artistId, FileOwnerType.ARTIST, imageUris)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeRenewalStatusArtistImagesEventHandler(event: ArtistUpdatedEvent) {
        val artist = event.artist
        val artistId = artist.identifier
        val imageUris = listOf(artist.profileImage, artist.backgroundImageUrl)
        uploadFileStatusChangeService.changeRenewal(artistId, FileOwnerType.ARTIST, imageUris)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeAbandonedStatusArtistImagesEventHandler(event: ArtistDeletedEvent) {
        uploadFileStatusChangeService.changeAllAbandoned(event.artistId, FileOwnerType.ARTIST)
    }
}
