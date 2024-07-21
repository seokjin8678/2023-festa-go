package com.festago.upload.infrastructure

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import com.festago.upload.domain.FileExtension
import com.festago.upload.domain.StorageClient
import com.festago.upload.domain.UploadFile
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.URI
import java.time.Clock
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import software.amazon.awssdk.services.s3.model.PutObjectRequest

private val log = KotlinLogging.logger {}

@Component
class R2StorageClient(
    @Value("\${festago.r2.access-key}")
    accessKey: String,
    @Value("\${festago.r2.secret-key}")
    secretKey: String,
    @Value("\${festago.r2.endpoint}")
    endpoint: String,
    @Value("\${festago.r2.bucket}")
    bucket: String,
    @Value("\${festago.r2.url}")
    uri: String,
    clock: Clock
) : StorageClient {

    private final var s3Client: S3Client
    private final var uri: URI
    private final var bucket: String
    private final var clock: Clock

    init {
        val awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey)
        this.s3Client = S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of("auto"))
            .build()
        this.uri = URI.create(uri)
        this.bucket = bucket
        this.clock = clock
    }

    override fun storage(file: MultipartFile): UploadFile {
        val uploadFile = createUploadFile(file)
        upload(file, uploadFile)
        return uploadFile
    }

    private fun createUploadFile(file: MultipartFile): UploadFile {
        return UploadFile(file.size, uri, FileExtension.from(file.contentType), LocalDateTime.now(clock))
    }

    private fun upload(file: MultipartFile, uploadFile: UploadFile) {
        val objectRequest = PutObjectRequest.builder()
            .key(uploadFile.name)
            .bucket(bucket)
            .build()
        runCatching {
            file.inputStream.use { inputStream ->
                val fileSize = uploadFile.size
                val mimeType = uploadFile.mimeType.toString()
                val requestBody = RequestBody.fromContentProvider({ inputStream }, fileSize, mimeType)
                val uploadFileId = uploadFile.id
                log.info { "파일 업로드 시작. id=${uploadFileId}, uploadUri=${uploadFile.uploadUri}, size=${fileSize}" }
                s3Client.putObject(objectRequest, requestBody)
                log.info { "파일 업로드 완료. id=${uploadFileId}" }
            }
        }.onFailure { e ->
            log.warn { "파일 업로드 중 문제가 발생했습니다. id=${uploadFile.id}" }
            throw InternalServerException(ErrorCode.FILE_UPLOAD_ERROR, e)
        }
    }

    override fun delete(uploadFiles: List<UploadFile>) {
        if (uploadFiles.isEmpty()) {
            log.info { "삭제하려는 파일이 없습니다." }
            return
        }
        val fileSize = uploadFiles.size
        val firstFileId = uploadFiles.first().id
        val deleteObjectsRequest = getDeleteObjectsRequest(uploadFiles)

        log.info { "${fileSize}개 파일 삭제 시작. 첫 번째 파일 식별자=${firstFileId}" }
        val response = s3Client.deleteObjects(deleteObjectsRequest)
        log.info { "${fileSize}개 파일 삭제 완료. 첫 번째 파일 식별자=${firstFileId}" }

        if (response.hasErrors()) {
            val errors = response.errors()
            log.warn { "${fileSize}개 파일 삭제 중 에러가 발생했습니다. 첫 번째 파일 식별자=${firstFileId}, 에러 개수=${errors.size}" }
            errors.forEach {
                log.info { "파일 삭제 중 에러가 발생했습니다. key=${it.key()}, message=${it.message()}" }
            }
        }
    }

    private fun getDeleteObjectsRequest(uploadFiles: List<UploadFile>): DeleteObjectsRequest {
        val objectIdentifiers = uploadFiles.asSequence()
            .map { it.name }
            .map { ObjectIdentifier.builder().key(it).build() }
            .toList()
        return DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete { builder -> builder.objects(objectIdentifiers).build() }
            .build()
    }
}
