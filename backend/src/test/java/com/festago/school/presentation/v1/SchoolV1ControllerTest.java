package com.festago.school.presentation.v1;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.festago.school.application.v1.SchoolV1QueryService;
import com.festago.school.dto.v1.SchoolDetailV1Response;
import com.festago.school.dto.v1.SchoolFestivalV1Response;
import com.festago.school.dto.v1.SchoolSocialMediaV1Response;
import com.festago.school.infrastructure.repository.query.v1.SchoolFestivalV1SearchCondition;
import com.festago.socialmedia.domain.SocialMediaType;
import com.festago.support.CustomWebMvcTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@CustomWebMvcTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SchoolV1ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SchoolV1QueryService schoolV1QueryService;

    @Nested
    class 학교_상세_조회 {

        final String uri = "/api/v1/schools/{schoolId}";

        @Nested
        @DisplayName("GET " + uri)
        class 올바른_주소로 {

            @Test
            void 요청을_보내면_200_응답과_body가_반환된다() throws Exception {
                // given
                var expected = new SchoolDetailV1Response(
                    1L, "경북대학교",
                    "https://image.com/logo.png",
                    "https://image.com/backgroundLogo.png",
                    List.of(
                        new SchoolSocialMediaV1Response(SocialMediaType.YOUTUBE, "유튜브",
                            "https://image.com/youtube.png", "www.knu-youtube.com"),
                        new SchoolSocialMediaV1Response(SocialMediaType.INSTAGRAM, "인스타그램",
                            "https://image.com/youtube.png", "www.knu-instagram.com")
                    )
                );
                given(schoolV1QueryService.findDetailById(expected.getId()))
                    .willReturn(expected);

                // when & then
                mockMvc.perform(get(uri, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
            }
        }
    }

    @Nested
    class 학교_축제_조회 {

        final String uri = "/api/v1/schools/{schoolId}/festivals";

        @Nested
        @DisplayName("GET " + uri)
        class 올바른_주소로 {

            @Test
            void 요청을_보내면_200_응답과_body가_반환된다() throws Exception {
                // given
                var today = LocalDate.now();
                var searchCondition = new SchoolFestivalV1SearchCondition(null, null, false, Pageable.ofSize(10));
                var content = List.of(new SchoolFestivalV1Response(
                    1L, "경북대학교", today, today.plusDays(1), "www.image.com/image.png",
                    "아티스트"
                ));
                var slice = new SliceImpl<>(content, Pageable.ofSize(10), true);

                given(schoolV1QueryService.findFestivalsBySchoolId(1L, today, searchCondition))
                    .willReturn(slice);

                // when & then
                mockMvc.perform(get(uri, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
            }

            @Test
            void 요청시_페이지가_20을_넘어가면_예외() throws Exception {
                // given
                int maxPageSize = 20;

                // when && then
                mockMvc.perform(get(uri, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", String.valueOf(maxPageSize + 1)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            }
        }
    }
}
