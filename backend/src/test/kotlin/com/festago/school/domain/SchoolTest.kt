package com.festago.school.domain

import com.festago.common.exception.ValidException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

internal class SchoolTest {

    @Nested
    inner class 생성 {

        @Test
        fun 도메인이_50자를_넘으면_예외() {
            // given
            val domain = "1".repeat(51)

            // when & then
            shouldThrow<ValidException> {
                School(domain, "테코대학교", "", "", SchoolRegion.서울)
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun 도메인이_공백이면_예외(domain: String) {
            // when & then
            shouldThrow<ValidException> {
                School(domain, "테코대학교", "", "", SchoolRegion.서울)
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 50])
        fun 도메인이_50자_이내이면_성공(length: Int) {
            // given
            val domain = "1".repeat(length)

            // when
            val school = School(domain, "테코대학교", "", "", SchoolRegion.서울)

            // then
            school.domain shouldBe domain
        }

        @Test
        fun 이름이_255자를_넘으면_예외() {
            // given
            val name = "1".repeat(256)

            // when & then
            shouldThrow<ValidException> {
                School("teco.ac.kr", name, "", "", SchoolRegion.서울)
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun 이름이_공백이면_예외(name: String) {
            // when & then
            shouldThrow<ValidException> {
                School("teco.ac.kr", name, "", "", SchoolRegion.서울)
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 255])
        fun 이름이_255자_이내이면_성공(length: Int) {
            // given
            val name = "1".repeat(length)

            // when
            val school = School("teco.ac.kr", name, "", "", SchoolRegion.서울)

            // then
            school.name shouldBe name
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun logoUrl이_null_또는_공백이면_기본값이_할당된다(logoUrl: String?) {
            // when
            val school = School(
                id = 1L,
                domain = "teco.ac.kr",
                name = "테코대학교",
                logoUrl = logoUrl,
                backgroundImageUrl = "https://image.com/backgroundImage.png",
                region = SchoolRegion.서울
            )

            // then
            school.logoUrl shouldBe ""
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 255])
        fun logoUrl이_255자_이내이면_성공(length: Int) {
            // given
            val logoUrl = "1".repeat(length)

            // when
            val school = School(
                id = 1L,
                domain = "teco.ac.kr",
                name = "테코대학교",
                logoUrl = logoUrl,
                backgroundImageUrl = "https://image.com/backgroundImage.png",
                region = SchoolRegion.서울
            )

            // then
            school.logoUrl shouldBe logoUrl
        }

        @Test
        fun logoUrl이_255자를_넘으면_예외() {
            // given
            val logoUrl = "1".repeat(256)

            // when & then
            shouldThrow<ValidException> {
                School(
                    id = 1L,
                    domain = "teco.ac.kr",
                    name = "테코대학교",
                    logoUrl = logoUrl,
                    backgroundImageUrl = "https://image.com/backgroundImage.png",
                    region = SchoolRegion.서울
                )
            }
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun backgroundImageUrl이_null_또는_공백이면_기본값이_할당된다(backgroundImageUrl: String?) {
            // when
            val school = School(
                id = 1L,
                domain = "teco.ac.kr",
                name = "테코대학교",
                logoUrl = "https://image.com/logo.png",
                backgroundImageUrl = backgroundImageUrl,
                region = SchoolRegion.서울
            )

            // then
            school.backgroundUrl shouldBe ""
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 255])
        fun backgroundImageUrl이_255자_이내이면_성공(length: Int) {
            // given
            val backgroundImageUrl = "1".repeat(length)

            // when
            val school = School(
                id = 1L,
                domain = "teco.ac.kr",
                name = "테코대학교",
                logoUrl = "https://image.com/logo.png",
                backgroundImageUrl = backgroundImageUrl,
                region = SchoolRegion.서울
            )

            // then
            school.backgroundUrl shouldBe backgroundImageUrl
        }

        @Test
        fun backgroundImageUrl이_255자를_넘으면_예외() {
            // given
            val backgroundImageUrl = "1".repeat(256)

            // when & then
            shouldThrow<ValidException> {
                School(
                    id = 1L,
                    domain = "teco.ac.kr",
                    name = "테코대학교",
                    logoUrl = "https://image.com/logo.png",
                    backgroundImageUrl = backgroundImageUrl,
                    region = SchoolRegion.서울
                )
            }
        }

        @Test
        fun 성공() {
            // given
            val id = 1L
            val domain = "teco.ac.kr"
            val name = "테코대학교"
            val logoUrl = "https://image.com/logo.png"
            val backgroundImageUrl = "https://image.com/backgroundImage.png"
            val region = SchoolRegion.서울

            // when
            val school = School(id, domain, name, logoUrl, backgroundImageUrl, region)

            // then
            school.id shouldBe 1L
            school.domain shouldBe domain
            school.name shouldBe name
            school.logoUrl shouldBe logoUrl
            school.backgroundUrl shouldBe backgroundImageUrl
            school.region shouldBe region
        }
    }

    @Nested
    inner class 수정 {
        lateinit var school: School

        @BeforeEach
        fun setUp() {
            school = School(
                id = 1L,
                domain = "teco.ac.kr",
                name = "테코대학교",
                logoUrl = "https://image.com/logo.png",
                backgroundImageUrl = "https://image.com/backgroundImage.png",
                region = SchoolRegion.서울
            )
        }

        @Test
        fun 도메인이_51자를_넘으면_예외() {
            // given
            val domain = "1".repeat(51)

            // when & then
            shouldThrow<ValidException> {
                school.changeDomain(domain)
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun 도메인이_공백이면_예외(domain: String) {
            // when & then
            shouldThrow<ValidException> {
                school.changeDomain(domain)
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 50])
        fun 도메인이_50자_이내이면_성공(length: Int) {
            // given
            val domain = "1".repeat(length)

            // when
            school.changeDomain(domain)

            // then
            school.domain shouldBe domain
        }

        @Test
        fun 이름이_255자를_넘으면_예외() {
            // given
            val name = "1".repeat(256)

            // when & then
            shouldThrow<ValidException> {
                school.changeDomain(name)
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun 이름이_공백이면_예외(name: String) {
            // when & then
            shouldThrow<ValidException> {
                school.changeDomain(name)
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 255])
        fun 이름이_255자_이내이면_성공(length: Int) {
            // given
            val name = "1".repeat(length)

            // when
            school.changeName(name)

            // then
            school.name shouldBe name
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun logoUrl이_null_또는_공백이면_기본값이_할당된다(logoUrl: String?) {
            // when
            school.changeLogoUrl(logoUrl)

            // then
            school.logoUrl shouldBe ""
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 255])
        fun logoUrl이_255글자_이내이면_성공(length: Int) {
            val logoUrl = "1".repeat(length)

            // when
            school.changeLogoUrl(logoUrl)

            // then
            school.logoUrl shouldBe logoUrl
        }

        @Test
        fun logoUrl이_255자를_넘으면_예외() {
            // given
            val logoUrl = "1".repeat(256)

            // when & then
            shouldThrow<ValidException> {
                school.changeLogoUrl(logoUrl)
            }
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = ["", " ", "\t", "\n"])
        fun backgroundImageUrl이_null_또는_공백이면_기본값이_할당된다(backgroundImageUrl: String?) {
            // when
            school.changeBackgroundImageUrl(backgroundImageUrl)

            // then
            school.backgroundUrl shouldBe ""
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 255])
        fun backgroundImageUrl이_255글자_이내이면_성공(length: Int) {
            // given
            val backgroundImageUrl = "1".repeat(length)

            // when
            school.changeBackgroundImageUrl(backgroundImageUrl)

            // then
            school.backgroundUrl shouldBe backgroundImageUrl
        }

        @Test
        fun backgroundImageUrl이_255자를_넘으면_예외() {
            // given
            val backgroundImageUrl = "1".repeat(256)

            // when & then
            shouldThrow<ValidException> {
                school.changeBackgroundImageUrl(backgroundImageUrl)
            }
        }
    }
}
