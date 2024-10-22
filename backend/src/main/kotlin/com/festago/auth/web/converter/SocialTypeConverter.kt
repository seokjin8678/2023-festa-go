package com.festago.auth.web.converter

import com.festago.auth.domain.SocialType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
internal class SocialTypeConverter : Converter<String, SocialType> {

    override fun convert(socialType: String): SocialType {
        return SocialType.valueOf(socialType.uppercase())
    }
}
