package com.festago.admin.presentation

import com.festago.admin.application.AdminActuatorProxyService
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
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

    @GetMapping("/{path}")
    fun getActuator(@PathVariable path: String): ResponseEntity<String> {
        return adminActuatorProxyService.request(path)
    }
}
