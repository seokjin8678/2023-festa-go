package com.festago.upload.application.festival

import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.dto.event.FestivalUpdatedEvent
import com.festago.upload.application.UploadFileStatusChangeService
import com.festago.upload.domain.FileOwnerType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Async
@Component
class AsyncFestivalUploadImagesStatusChangeEventListener(
    private val uploadFileStatusChangeService: UploadFileStatusChangeService
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeAttachedStatusFestivalImagesEventHandler(event: FestivalCreatedEvent) {
        val festival = event.festival
        val festivalId = festival.id!!
        val imageUris = listOf(festival.posterImageUrl)
        uploadFileStatusChangeService.changeAttached(festivalId, FileOwnerType.FESTIVAL, imageUris)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeRenewalStatusFestivalImagesEventHandler(event: FestivalUpdatedEvent) {
        val festival = event.festival
        val festivalId = festival.id!!
        val imageUris = listOf(festival.posterImageUrl)
        uploadFileStatusChangeService.changeRenewal(festivalId, FileOwnerType.FESTIVAL, imageUris)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeAbandonedStatusFestivalImagesEventHandler(event: FestivalDeletedEvent) {
        uploadFileStatusChangeService.changeAllAbandoned(event.festivalId, FileOwnerType.FESTIVAL)
    }
}
