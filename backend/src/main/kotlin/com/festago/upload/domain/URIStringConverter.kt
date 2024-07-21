package com.festago.upload.domain

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.net.URI

@Converter
class URIStringConverter : AttributeConverter<URI, String> {
    override fun convertToDatabaseColumn(attribute: URI): String {
        return attribute.toString()
    }

    override fun convertToEntityAttribute(dbData: String): URI {
        return URI.create(dbData)
    }
}
