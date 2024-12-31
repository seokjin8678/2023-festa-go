package com.festago.admin.presentation

import com.festago.admin.application.AdminActuatorProxyService
import com.festago.common.annotation.LoggingDetail
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/actuator")
class AdminActuatorController(
    private val adminActuatorProxyService: AdminActuatorProxyService,
) {

    @LoggingDetail(hideResponseBody = true)
    @GetMapping("/{path}")
    fun getActuator(@PathVariable path: String): ByteArray {
        return adminActuatorProxyService.request(path)
    }
}
