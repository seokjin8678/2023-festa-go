package com.festago.auth.infrastructure.oauth2.kakao

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.charset.StandardCharsets
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler

private val log = KotlinLogging.logger {}

internal object KakaoOAuth2TokenClientErrorHandler : DefaultResponseErrorHandler() {

    private val objectMapper = jacksonObjectMapper()

    override fun handleError(response: ClientHttpResponse) {
        val statusCode = response.statusCode
        val responseBody = getResponseBody(response).toString(getCharset(response) ?: StandardCharsets.UTF_8)
        val errorResponse = getErrorResponse(responseBody)
        if (statusCode.is4xxClientError) {
            if (errorResponse.isErrorCodeKOE320()) {
                throw BadRequestException(ErrorCode.OAUTH2_INVALID_CODE)
            }
            log.warn { "카카오 OAuth2 서버에서 ${statusCode.value()} 상태코드가 반환되었습니다. response: $responseBody" }
            throw InternalServerException(ErrorCode.OAUTH2_INVALID_REQUEST)
        }
        log.warn { "카카오 OAuth2 서버에서 ${statusCode.value()} 상태코드가 반환되었습니다. response: $responseBody" }
        throw InternalServerException(ErrorCode.OAUTH2_PROVIDER_NOT_RESPONSE)
    }

    private fun getErrorResponse(responseBody: String): KakaoOAuth2ErrorResponse {
        return if (responseBody.isEmpty()) {
            KakaoOAuth2ErrorResponse.EMPTY
        } else {
            objectMapper.readValue<KakaoOAuth2ErrorResponse>(responseBody)
        }
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
private data class KakaoOAuth2ErrorResponse(
    val error: String? = null,
    val errorDescription: String? = null,
    val errorCode: String? = null,
) {
    companion object {
        val EMPTY = KakaoOAuth2ErrorResponse()
    }
}

private fun KakaoOAuth2ErrorResponse.isErrorCodeKOE320() = errorCode == "KOE320"
