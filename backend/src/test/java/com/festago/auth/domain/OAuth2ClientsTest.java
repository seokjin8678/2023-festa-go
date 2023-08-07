package com.festago.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.festago.auth.domain.OAuth2Clients.OAuth2ClientsBuilder;
import com.festago.auth.infrastructure.FestagoOAuth2Client;
import com.festago.auth.infrastructure.KakaoOAuth2AccessTokenClient;
import com.festago.auth.infrastructure.KakaoOAuth2Client;
import com.festago.auth.infrastructure.KakaoOAuth2UserInfoClient;
import com.festago.exception.BadRequestException;
import com.festago.exception.InternalServerException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class OAuth2ClientsTest {

    @Test
    void 중복된_타입의_클라이언트가_주어지면_예외() {
        // given
        OAuth2ClientsBuilder builder = OAuth2Clients.builder()
            .add(new FestagoOAuth2Client());

        // when & then
        assertThatThrownBy(() -> builder.add(new FestagoOAuth2Client()))
            .isInstanceOf(InternalServerException.class)
            .hasMessage("중복된 OAuth2 제공자 입니다.");
    }

    @Test
    void 해당_타입의_클라이언트가_없으면_예외() {
        // given
        OAuth2Clients oAuth2Clients = OAuth2Clients.builder()
            .build();

        // when & then
        assertThatThrownBy(() -> oAuth2Clients.getClient(SocialType.FESTAGO))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("해당 OAuth2 제공자는 지원되지 않습니다.");
    }

    @Test
    void 여러_타입의_클라이언트가_주어졌을때_타입으로_찾기_성공() {
        // given
        FestagoOAuth2Client festagoOAuth2Client = new FestagoOAuth2Client();
        KakaoOAuth2Client kakaoOAuth2Client = new KakaoOAuth2Client(
            mock(KakaoOAuth2AccessTokenClient.class),
            mock(KakaoOAuth2UserInfoClient.class)
        );
        OAuth2Clients oAuth2Clients = OAuth2Clients.builder()
            .add(festagoOAuth2Client)
            .add(kakaoOAuth2Client)
            .build();

        // when
        OAuth2Client expect = oAuth2Clients.getClient(SocialType.KAKAO);

        // then
        assertThat(expect).isExactlyInstanceOf(KakaoOAuth2Client.class);
    }
}