package com.festago.festival.presentation.v1

import com.festago.festival.application.query.FestivalDetailV1QueryService
import com.festago.festival.application.query.FestivalV1QueryService
import com.festago.festival.dto.FestivalDetailV1Response
import com.festago.festival.dto.SchoolV1Response
import com.festago.support.spec.ControllerDescribeSpec
import io.mockk.every
import java.time.LocalDate
import org.springframework.data.domain.SliceImpl
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

class FestivalV1ControllerTestKt(
    val mockMvc: MockMvc,
    val festivalV1QueryService: FestivalV1QueryService,
    val festivalDetailV1QueryService: FestivalDetailV1QueryService,
) : ControllerDescribeSpec({

    describe("축제 목록 커서 기반 페이징 조회") {

        val uri = "/api/v1/festivals"

        context("GET $uri") {

            every { festivalV1QueryService.findFestivals(any(), any()) } returns SliceImpl(emptyList())

            it("쿼리 파라미터에 festivalId와 lastStartDate를 모두 보내면 200 응답이 반환된다") {
                mockMvc.get(uri) {
                    contentType = MediaType.APPLICATION_JSON
                    param(LAST_FESTIVAL_ID_KEY, "1")
                    param(LAST_START_DATE_KEY, "1999-10-01")
                }.andExpect {
                    status { isOk() }
                }
            }

            it("쿼리 파라미터에 festivalId와 lastStartDate가 없어도 200 응답이 반환된다") {
                mockMvc.get(uri) {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }
            }

            it("쿼리 파라미터에 size가 20을 초과하면 예외가 발생한다") {
                mockMvc.get(uri) {
                    contentType = MediaType.APPLICATION_JSON
                    param(SIZE_KEY, "21")
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.message") { value("최대 size 값을 초과했습니다.") }
                }
            }
        }
    }

    describe("축제 상세 조회") {

        val uri = "/api/v1/festivals/{festivalId}"
        val festivalId = 1L

        context("GET $uri") {

            every { festivalDetailV1QueryService.findFestivalDetail(any(Long::class)) } returns FestivalDetailV1Response(
                id = festivalId,
                name = "테코대학교 축제",
                school = SchoolV1Response(
                    id = 1L,
                    name = "테코대학교"
                ),
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                posterImageUrl = "https://image.com/imgae.png",
                stages = emptySet(),
                socialMedias = emptySet(),
            )

            it("축제 상세 정보와 200 응답이 반환된다") {
                mockMvc.get(uri, festivalId) {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }
            }
        }
    }
}) {
    companion object {
        const val LAST_FESTIVAL_ID_KEY = "lastFestivalId"
        const val LAST_START_DATE_KEY = "lastStartDate"
        const val SIZE_KEY = "size"
    }
}