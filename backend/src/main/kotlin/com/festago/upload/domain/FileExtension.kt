package com.festago.upload.domain

import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils

enum class FileExtension(
    val value: String,
    val mimeType: MimeType,
) {
    JPG(".jpg", MimeTypeUtils.IMAGE_JPEG),
    JPEG(".jpeg", MimeTypeUtils.IMAGE_JPEG),
    PNG(".png", MimeTypeUtils.IMAGE_PNG),
    NONE("", MimeTypeUtils.APPLICATION_OCTET_STREAM),
    ;

    companion object {
        fun from(mimeType: String?): FileExtension {
            if (mimeType.isNullOrBlank()) {
                return NONE
            }
            return FileExtension.entries
                .find { it.mimeType.toString() == mimeType } ?: NONE
        }
    }
}
