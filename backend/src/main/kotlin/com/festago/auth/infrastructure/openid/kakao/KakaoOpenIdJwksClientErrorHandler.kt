package com.festago.auth.infrastructure.openid.kakao

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.charset.StandardCharsets
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler

private val log = KotlinLogging.logger {}

internal object KakaoOpenIdJwksClientErrorHandler : DefaultResponseErrorHandler() {

    override fun handleError(response: ClientHttpResponse) {
        val statusCode = response.statusCode
        val responseBody = getResponseBody(response).toString(getCharset(response) ?: StandardCharsets.UTF_8)
        if (statusCode.is4xxClientError) {
            log.warn { "카카오 JWKS 서버에서 ${statusCode.value()} 상태코드가 반환되었습니다. response: $responseBody" }
            throw InternalServerException(ErrorCode.OPEN_ID_PROVIDER_NOT_RESPONSE)
        }
        log.error { "카카오 JWKS 서버에서 알 수 없는 에러가 발생했습니다. response: $responseBody" }
        throw InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}
