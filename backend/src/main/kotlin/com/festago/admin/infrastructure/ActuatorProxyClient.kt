package com.festago.admin.infrastructure

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import com.festago.common.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestClient

@Component
class ActuatorProxyClient(
    @Value("\${management.server.port}")
    private val port: String,
    restTemplateBuilder: RestTemplateBuilder,
) {
    private val restClient = RestClient.builder(restTemplateBuilder.build())
        .baseUrl("http://localhost:$port/actuator/")
        .build()

    fun request(path: String): ByteArray {
        return restClient.get()
            .uri(path)
            .retrieve()
            .onStatus(AdminActuatorProxyErrorHandler)
            .body(ByteArray::class.java) ?: ByteArray(0)
    }
}

private object AdminActuatorProxyErrorHandler : DefaultResponseErrorHandler() {

    override fun handleError(response: ClientHttpResponse) {
        val statusCode = response.statusCode
        if (statusCode.is4xxClientError) {
            throw NotFoundException(ErrorCode.ACTUATOR_NOT_FOUND)
        }
        throw InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}
