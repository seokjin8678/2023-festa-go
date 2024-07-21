package com.festago.upload.util

import io.kotest.core.spec.style.AnnotationSpec
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource

class UriUploadFileIdParserTest : AnnotationSpec() {

    @ParameterizedTest
    @NullAndEmptySource
    fun URI가_null_또는_빈_문자열이면_null이_반환된다(uri: String?) {
        // when
        val actual = UriUploadFileIdParser.parse(uri)
        // then
        assertThat(actual).isNull()
    }

    @Test
    fun UUID_형식이라도_확장자가_존재하지_않으면_null이_반환된다() {
        // given
        val uri = "https://image.com/" + UUID.randomUUID()

        // when
        val actual = UriUploadFileIdParser.parse(uri)

        // then
        assertThat(actual).isNull()
    }

    @Test
    fun UUID_형식이_아니면_null이_반환된다() {
        // given
        val uri = "https://image.com/image.png"

        // when
        val actual = UriUploadFileIdParser.parse(uri)

        // then
        assertThat(actual).isNull()
    }

    @Test
    fun URI의_형식에_스키마가_존재하지_않아도_null이_반환되지_않는다() {
        // given
        val uri = UUID.randomUUID().toString() + ".png"

        // when
        val actual = UriUploadFileIdParser.parse(uri)

        // then
        assertThat(actual).isNotNull()
    }

    @Test
    fun 파일_이름이_UUID_형식이면_null이_반환되지_않는다() {
        // given
        val uri = "https://image.com/" + UUID.randomUUID() + ".png"

        // when
        val actual = UriUploadFileIdParser.parse(uri)

        // then
        assertThat(actual).isNotNull()
    }
}
