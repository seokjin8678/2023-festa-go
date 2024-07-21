package com.festago.upload.application.school

import com.festago.school.dto.event.SchoolCreatedEvent
import com.festago.school.dto.event.SchoolDeletedEvent
import com.festago.school.dto.event.SchoolUpdatedEvent
import com.festago.upload.application.UploadFileStatusChangeService
import com.festago.upload.domain.FileOwnerType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Async
@Component
class AsyncSchoolUploadImagesStatusChangeEventListener(
    private val uploadFileStatusChangeService: UploadFileStatusChangeService
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeAttachedStatusSchoolImagesEventHandler(event: SchoolCreatedEvent) {
        val school = event.school
        val schoolId = school.id
        val imageUris = listOf(school.backgroundUrl, school.logoUrl)
        uploadFileStatusChangeService.changeAttached(schoolId, FileOwnerType.SCHOOL, imageUris)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeRenewalStatusSchoolImagesEventHandler(event: SchoolUpdatedEvent) {
        val school = event.school
        val schoolId = school.id
        val imageUris = listOf(school.backgroundUrl, school.logoUrl)
        uploadFileStatusChangeService.changeRenewal(schoolId, FileOwnerType.SCHOOL, imageUris)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun changeAbandonedStatusSchoolImagesEventHandler(event: SchoolDeletedEvent) {
        uploadFileStatusChangeService.changeAllAbandoned(event.schoolId, FileOwnerType.SCHOOL)
    }
}
