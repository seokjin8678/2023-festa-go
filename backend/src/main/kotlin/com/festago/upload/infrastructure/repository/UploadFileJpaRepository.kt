package com.festago.upload.infrastructure.repository

import com.festago.upload.domain.FileOwnerType
import com.festago.upload.domain.UploadFile
import com.festago.upload.domain.UploadStatus
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface UploadFileJpaRepository : JpaRepository<UploadFile, UUID> {

    fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: FileOwnerType): List<UploadFile>

    fun findByIdIn(ids: Collection<UUID>): List<UploadFile>

    fun findByCreatedAtBetweenAndStatus(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        status: UploadStatus,
    ): List<UploadFile>

    fun findByCreatedAtBeforeAndStatus(createdAt: LocalDateTime, status: UploadStatus): List<UploadFile>

    @Modifying
    @Query("DELETE FROM UploadFile uf WHERE uf IN :uploadFiles")
    fun deleteByIn(@Param("uploadFiles") uploadFiles: List<UploadFile>)
}
