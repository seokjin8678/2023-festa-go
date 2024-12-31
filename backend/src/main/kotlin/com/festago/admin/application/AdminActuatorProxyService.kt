package com.festago.admin.application

import com.festago.admin.infrastructure.ActuatorProxyClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AdminActuatorProxyService(
    private val actuatorProxyClient: ActuatorProxyClient,
) {

    fun request(path: String): ResponseEntity<ByteArray> {
        return actuatorProxyClient.request(path).let {
            if (path != "heapdump") {
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(it)
            } else {
                ResponseEntity.ok(it)
            }
        }
    }
}
