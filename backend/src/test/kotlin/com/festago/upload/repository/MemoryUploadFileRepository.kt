package com.festago.upload.repository

import com.festago.upload.domain.FileOwnerType
import com.festago.upload.domain.UploadFile
import com.festago.upload.domain.UploadStatus
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

class MemoryUploadFileRepository : UploadFileRepository {
    private val memory = HashMap<UUID, UploadFile>()

    override fun save(uploadFile: UploadFile): UploadFile {
        memory[uploadFile.id] = uploadFile
        return uploadFile
    }

    override fun findById(id: UUID): Optional<UploadFile> {
        return Optional.ofNullable(memory[id])
    }

    override fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: FileOwnerType): List<UploadFile> {
        return memory.values.stream()
            .filter { it.ownerId == ownerId && it.ownerType == ownerType }
            .toList()
    }

    override fun findByIdIn(ids: Collection<UUID>): List<UploadFile> {
        return memory.values.filter { ids.contains(it.id) }
    }

    override fun findByCreatedAtBetweenAndStatus(
        startTime: LocalDateTime, endTime: LocalDateTime,
        status: UploadStatus,
    ): List<UploadFile> {
        return memory.values.asSequence()
            .filter { it.status == status }
            .filter { it.createdAt in startTime..endTime }
            .toList()
    }

    override fun findByCreatedAtBeforeAndStatus(createdAt: LocalDateTime, status: UploadStatus): List<UploadFile> {
        return memory.values.asSequence()
            .filter { it.status == status }
            .filter { it.createdAt < createdAt }
            .toList()
    }

    override fun deleteByIn(uploadFiles: List<UploadFile>) {
        for (uploadFile in uploadFiles) {
            memory.remove(uploadFile.id)
        }
    }
}
