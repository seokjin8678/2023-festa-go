package com.festago.festival.domain;

import static com.festago.common.exception.ErrorCode.INVALID_FESTIVAL_DURATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.festago.common.exception.BadRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class FestivalDurationTest {

    LocalDate _6월_15일 = LocalDate.parse("2077-06-15");
    LocalDate _6월_16일 = LocalDate.parse("2077-06-16");
    LocalDate _6월_17일 = LocalDate.parse("2077-06-17");

    @Test
    void 시작일이_종료일_이전이면_예외() {
        // when & then
        assertThatThrownBy(() -> new FestivalDuration(_6월_17일, _6월_16일))
            .isInstanceOf(BadRequestException.class)
            .hasMessage(INVALID_FESTIVAL_DURATION.getMessage());
    }

    @Nested
    class isBeforeStartDate {

        @Test
        void 기간의_시작일이_주어진_날짜보다_이후이면_거짓() {
            // given
            FestivalDuration _6월_16일_6월_16일 = new FestivalDuration(_6월_16일, _6월_16일);

            // then
            boolean actual = _6월_16일_6월_16일.isStartDateBeforeTo(_6월_15일);

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void 시작일이_주어진_날짜에_포함되면_거짓() {
            // given
            FestivalDuration _6월_16일_6월_16일 = new FestivalDuration(_6월_16일, _6월_16일);

            // then
            boolean actual = _6월_16일_6월_16일.isStartDateBeforeTo(_6월_16일);

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void 시작일이_주어진_날짜_이전이면_참() {
            // given
            FestivalDuration _6월_16일_6월_16일 = new FestivalDuration(_6월_16일, _6월_16일);

            // then
            boolean actual = _6월_16일_6월_16일.isStartDateBeforeTo(_6월_17일);

            // then
            assertThat(actual).isTrue();
        }
    }

    @Nested
    class isNotInDuration {

        @Test
        void 기간에_포함되면_거짓() {
            // given
            FestivalDuration _6월_15일_6월_17일 = new FestivalDuration(_6월_15일, _6월_17일);

            // when
            boolean actual = _6월_15일_6월_17일.isNotInDuration(_6월_16일);

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void 기간에_포함되지_않으면_참() {
            // given
            FestivalDuration _6월_16일_6월_17일 = new FestivalDuration(_6월_16일, _6월_17일);

            // when
            boolean actual = _6월_16일_6월_17일.isNotInDuration(_6월_15일);

            // then
            assertThat(actual).isTrue();
        }
    }
}
