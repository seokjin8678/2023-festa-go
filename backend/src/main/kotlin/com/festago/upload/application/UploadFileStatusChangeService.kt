package com.festago.upload.application

import com.festago.upload.domain.FileOwnerType
import com.festago.upload.repository.UploadFileRepository
import com.festago.upload.util.UriUploadFileIdParser
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UploadFileStatusChangeService(
    private val uploadFileRepository: UploadFileRepository
) {

    /**
     * 인자로 들어오는 fileUris에 해당하는 UploadFile을 모두 찾아 해당 UploadFile의 주인을 설정하고, ATTACHED 상태로 변경한다. <br></br>
     *
     * @param ownerId   상태를 변경할 UploadFile의 주인 식별자
     * @param ownerType 상태를 변경할 UploadFile의 주인 타입
     * @param fileUris  주인을 설정하고 ATTACHED 상태로 변경할 UploadFileUri 목록
     */
    fun changeAttached(ownerId: Long, ownerType: FileOwnerType, fileUris: Collection<String>) {
        val uploadFileIds = parseFileIds(fileUris)
        uploadFileRepository.findByIdIn(uploadFileIds)
            .forEach { it.changeAttached(ownerId, ownerType) }
    }

    private fun parseFileIds(fileUris: Collection<String>): Set<UUID> {
        return fileUris.asSequence()
            .mapNotNull { UriUploadFileIdParser.parse(it) }
            .toSet()
    }

    /**
     * 인자로 들어오는 ownerId와 ownerType에 해당하는 UploadFile을 모두 찾고 상태를 새롭게 변경한다. <br></br>
     *
     * @param ownerId   상태를 변경할 UploadFile의 주인 식별자
     * @param ownerType 상태를 변경할 UploadFile의 주인 타입
     * @param fileUris  새롭게 변경된 UploadFileUri 목록
     */
    fun changeRenewal(ownerId: Long, ownerType: FileOwnerType, fileUris: Collection<String>) {
        val uploadFileIds = parseFileIds(fileUris)
        uploadFileRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType)
            .forEach { it.renewalStatus(ownerId, ownerType, uploadFileIds) }
    }

    /**
     * 인자로 들어오는 ownerId와 ownerType에 해당하는 UploadFile을 모두 찾고 ABANDONED 상태로 변경한다.
     *
     * @param ownerId   상태를 변경할 UploadFile의 주인 식별자
     * @param ownerType 상태를 변경할 UploadFile의 주인 타입
     */
    fun changeAllAbandoned(ownerId: Long, ownerType: FileOwnerType) {
        uploadFileRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType)
            .forEach { it.changeAbandoned() }
    }
}
