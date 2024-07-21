package com.festago.upload.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class FilenameExtensionParserTest {

    @Test
    fun 파일의_확장자가_없으면_빈_문자열이_반환된다() {
        // given
        val filename = "myFile"

        // when
        val extension = FileNameExtensionParser.parse(filename)

        // then
        Assertions.assertThat(extension).isEmpty()
    }

    @Test
    fun 파일의_이름에_점_뒤에_문자열이_없으면_빈_문자열이_반환된다() {
        // given
        val filename = "myFile."

        // when
        val extension = FileNameExtensionParser.parse(filename)

        // then
        Assertions.assertThat(extension).isEmpty()
    }

    @Test
    fun 파일의_확장자가_있으면_확장자가_반환된다() {
        // given
        val filename = "myFile.png"

        // when
        val extension = FileNameExtensionParser.parse(filename)

        // then
        Assertions.assertThat(extension).isEqualTo(".png")
    }

    @Test
    fun 파일의_이름에_점이_여러개면_마지막_점_기준_확장자가_반환된다() {
        // given
        val filename = "myFile.jpg.png"

        // when
        val extension = FileNameExtensionParser.parse(filename)

        // then
        Assertions.assertThat(extension).isEqualTo(".png")
    }

    @Test
    fun 파일의_마지막에_공백이_있어도_확장자가_반환된다() {
        // given
        val filename = "myFile.png   "

        // when
        val extension = FileNameExtensionParser.parse(filename)

        // then
        Assertions.assertThat(extension).isEqualTo(".png")
    }
}
