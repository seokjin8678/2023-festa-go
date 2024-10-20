package com.festago.admin.application

import com.festago.admin.infrastructure.ActuatorProxyClient
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AdminActuatorProxyService(
    private val actuatorProxyClient: ActuatorProxyClient,
) {

    fun request(path: String): ResponseEntity<String> {
        return actuatorProxyClient.request(path)
    }
}
