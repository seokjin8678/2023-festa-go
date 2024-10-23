package com.festago.upload.application

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.util.Validator
import com.festago.upload.domain.FileExtension
import com.festago.upload.domain.FileOwnerType
import com.festago.upload.domain.StorageClient
import com.festago.upload.dto.FileUploadResult
import com.festago.upload.repository.UploadFileRepository
import com.festago.upload.util.FileNameExtensionParser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

private val log = KotlinLogging.logger {}

@Service
//@Transactional // 명시적으로 @Transactional 사용하지 않음
class ImageFileUploadService(
    private val storageClient: StorageClient,
    private val uploadFileRepository: UploadFileRepository,
) {

    fun upload(image: MultipartFile, ownerId: Long?, ownerType: FileOwnerType?): FileUploadResult {
        validate(image)
        val uploadImage = storageClient.storage(image)
        if (ownerId != null && ownerType != null) {
            uploadImage.changeAssigned(ownerId, ownerType)
        }
        uploadFileRepository.save(uploadImage)
        return FileUploadResult(uploadImage.id, uploadImage.uploadUri)
    }

    private fun validate(image: MultipartFile) {
        validateSize(image.size)
        validateExtension(image.originalFilename)
    }

    private fun validateSize(imageSize: Long) {
        Validator.maxValue(imageSize, MAX_FILE_SIZE, "imageSize")
    }

    private fun validateExtension(imageName: String?) {
        Validator.notBlank(imageName, "imageName")
        val extension = FileNameExtensionParser.parse(imageName)
        if (extension in ALLOW_IMAGE_EXTENSION) {
            return
        }
        log.info { "허용되지 않은 확장자에 대한 이미지 업로드 요청이 있습니다. fileName=${imageName}, extension=${extension}" }
        throw BadRequestException(ErrorCode.NOT_SUPPORT_FILE_EXTENSION)
    }

    companion object {
        private const val MAX_FILE_SIZE = 2_000_000L // 2MB
        private val ALLOW_IMAGE_EXTENSION = setOf(
            FileExtension.JPG.value,
            FileExtension.JPEG.value,
            FileExtension.PNG.value
        )
    }
}
