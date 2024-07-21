package com.festago.upload.infrastructure

import com.festago.support.fixture.UploadFileFixture
import com.festago.upload.domain.StorageClient
import com.festago.upload.domain.UploadFile
import org.springframework.web.multipart.MultipartFile

class FakeStorageClient : StorageClient {
    override fun storage(file: MultipartFile): UploadFile {
        return UploadFileFixture.builder().build()
    }

    override fun delete(uploadFiles: List<UploadFile>) {
        // NOOP
    }
}
