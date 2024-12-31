package com.festago.admin.application

import com.festago.admin.infrastructure.ActuatorProxyClient
import org.springframework.stereotype.Service

@Service
class AdminActuatorProxyService(
    private val actuatorProxyClient: ActuatorProxyClient,
) {

    fun request(path: String): ByteArray {
        return actuatorProxyClient.request(path)
    }
}
