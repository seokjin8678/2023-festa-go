package com.festago.upload.infrastructure.repository

import com.festago.upload.domain.FileOwnerType
import com.festago.upload.domain.UploadFile
import com.festago.upload.domain.UploadFileRepository
import com.festago.upload.domain.UploadStatus
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class UploadFileRepositoryImpl(
    private val uploadFileJpaRepository: UploadFileJpaRepository,
) : UploadFileRepository {

    override fun save(uploadFile: UploadFile): UploadFile {
        return uploadFileJpaRepository.save(uploadFile)
    }

    override fun findById(id: UUID): UploadFile? {
        return uploadFileJpaRepository.findByIdOrNull(id)
    }

    override fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: FileOwnerType): List<UploadFile> {
        return uploadFileJpaRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType)
    }

    override fun findByIdIn(ids: Collection<UUID>): List<UploadFile> {
        return uploadFileJpaRepository.findByIdIn(ids)
    }

    override fun findByCreatedAtBetweenAndStatus(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        status: UploadStatus,
    ): List<UploadFile> {
        return uploadFileJpaRepository.findByCreatedAtBetweenAndStatus(
            startTime = startTime,
            endTime = endTime,
            status = status
        )
    }

    override fun findByCreatedAtBeforeAndStatus(createdAt: LocalDateTime, status: UploadStatus): List<UploadFile> {
        return uploadFileJpaRepository.findByCreatedAtBeforeAndStatus(createdAt, status)
    }

    override fun deleteByIn(uploadFiles: List<UploadFile>) {
        uploadFileJpaRepository.deleteByIn(uploadFiles)
    }
}
