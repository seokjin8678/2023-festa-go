package com.festago.upload.application

import com.festago.upload.domain.StorageClient
import com.festago.upload.domain.UploadFile
import com.festago.upload.domain.UploadStatus
import com.festago.upload.repository.UploadFileRepository
import java.time.Clock
import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UploadFileDeleteService(
    private val storageClient: StorageClient,
    private val uploadFileRepository: UploadFileRepository,
    private val clock: Clock,

    ) {

    fun deleteAbandonedStatusWithPeriod(startTime: LocalDateTime, endTime: LocalDateTime) {
        val uploadFiles =
            uploadFileRepository.findByCreatedAtBetweenAndStatus(startTime, endTime, UploadStatus.ABANDONED)
        deleteUploadFiles(uploadFiles)
    }

    private fun deleteUploadFiles(uploadFiles: List<UploadFile>) {
        storageClient.delete(uploadFiles)
        uploadFileRepository.deleteByIn(uploadFiles)
    }

    fun deleteOldUploadedStatus() {
        val yesterday = LocalDateTime.now(clock).minusDays(1)
        val uploadFiles = uploadFileRepository.findByCreatedAtBeforeAndStatus(yesterday, UploadStatus.UPLOADED)
        deleteUploadFiles(uploadFiles)
    }
}
