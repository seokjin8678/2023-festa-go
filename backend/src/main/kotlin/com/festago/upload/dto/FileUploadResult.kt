package com.festago.upload.dto

import java.net.URI
import java.util.UUID

data class FileUploadResult(
    val uploadFileId: UUID,
    val uploadUri: URI
) {
    fun uploadFileId(): UUID {
        return uploadFileId
    }

    fun uploadUri(): URI {
        return uploadUri
    }
}

