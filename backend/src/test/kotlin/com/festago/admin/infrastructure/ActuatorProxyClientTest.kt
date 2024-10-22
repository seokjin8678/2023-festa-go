package com.festago.admin.infrastructure

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import com.festago.common.exception.NotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(ActuatorProxyClient::class)
internal class ActuatorProxyClientTest {

    @Autowired
    lateinit var actuatorProxyClient: ActuatorProxyClient

    @Autowired
    lateinit var mockServer: MockRestServiceServer

    @Test
    fun 상태코드가_4xx이면_NotFound_예외() {
        // given
        mockServer.expect(requestTo(URI))
            .andRespond(withBadRequest().contentType(MediaType.APPLICATION_JSON))

        // when
        val ex = shouldThrow<NotFoundException> {
            actuatorProxyClient.request("health")
        }

        // then
        ex shouldHaveMessage ErrorCode.ACTUATOR_NOT_FOUND.message
    }

    @Test
    fun 상태코드가_5xx이면_InternalServer_예외() {
        // given
        mockServer.expect(requestTo(URI))
            .andRespond(withServerError().contentType(MediaType.APPLICATION_JSON))

        // when
        val ex = shouldThrow<InternalServerException> {
            actuatorProxyClient.request("health")
        }

        // then
        ex shouldHaveMessage ErrorCode.INTERNAL_SERVER_ERROR.message
    }

    @Test
    fun 성공() {
        // given
        mockServer.expect(requestTo(URI))
            .andRespond(withSuccess().body("data").contentType(MediaType.APPLICATION_JSON))

        // when
        val response = actuatorProxyClient.request("health")

        // then
        response.body shouldBe "data"
    }

    companion object {
        private const val URI = "http://localhost:8090/actuator/health"
    }
}
