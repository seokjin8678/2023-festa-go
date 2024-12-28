package com.festago.upload.domain

import java.time.LocalDateTime
import java.util.UUID

interface UploadFileRepository {

    fun save(uploadFile: UploadFile): UploadFile

    fun findById(id: UUID): UploadFile?

    fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: FileOwnerType): List<UploadFile>

    fun findByIdIn(ids: Collection<UUID>): List<UploadFile>

    fun findByCreatedAtBetweenAndStatus(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        status: UploadStatus,
    ): List<UploadFile>

    fun findByCreatedAtBeforeAndStatus(createdAt: LocalDateTime, status: UploadStatus): List<UploadFile>

    fun deleteByIn(uploadFiles: List<UploadFile>)
}
