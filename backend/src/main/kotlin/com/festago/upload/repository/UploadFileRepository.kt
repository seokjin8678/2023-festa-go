package com.festago.upload.repository

import com.festago.upload.domain.FileOwnerType
import com.festago.upload.domain.UploadFile
import com.festago.upload.domain.UploadStatus
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface UploadFileRepository : Repository<UploadFile, UUID?> {
    fun save(uploadFile: UploadFile): UploadFile

    fun findById(id: UUID): Optional<UploadFile>

    fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: FileOwnerType): List<UploadFile>

    fun findByIdIn(ids: Collection<UUID>): List<UploadFile>

    fun findByCreatedAtBetweenAndStatus(
        startTime: LocalDateTime, endTime: LocalDateTime,
        status: UploadStatus
    ): List<UploadFile>

    fun findByCreatedAtBeforeAndStatus(createdAt: LocalDateTime, status: UploadStatus): List<UploadFile>

    @Modifying
    @Query("delete from UploadFile uf where uf in :uploadFiles")
    fun deleteByIn(@Param("uploadFiles") uploadFiles: List<UploadFile>)
}
