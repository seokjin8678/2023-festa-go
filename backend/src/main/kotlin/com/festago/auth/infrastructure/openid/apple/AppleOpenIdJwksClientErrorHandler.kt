package com.festago.auth.infrastructure.openid.apple

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.charset.StandardCharsets
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler

private val log = KotlinLogging.logger {}

internal object AppleOpenIdJwksClientErrorHandler : DefaultResponseErrorHandler() {

    override fun handleError(response: ClientHttpResponse) {
        val statusCode = response.statusCode
        val responseBody = getResponseBody(response).toString(getCharset(response) ?: StandardCharsets.UTF_8)
        if (statusCode.is4xxClientError) {
            log.warn { "Apple JWKS 서버에서 ${statusCode.value()} 상태코드가 반환되었습니다. response: $responseBody" }
            throw InternalServerException(ErrorCode.OPEN_ID_PROVIDER_NOT_RESPONSE)
        }
        log.error { "Apple JWKS 서버에서 알 수 없는 에러가 발생했습니다. response: $responseBody" }
        throw InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}
