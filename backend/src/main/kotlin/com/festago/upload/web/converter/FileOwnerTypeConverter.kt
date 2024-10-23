package com.festago.upload.web.converter

import com.festago.upload.domain.FileOwnerType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class FileOwnerTypeConverter : Converter<String, FileOwnerType> {
    override fun convert(fileOwnerType: String): FileOwnerType {
        return FileOwnerType.valueOf(fileOwnerType.uppercase())
    }
}
