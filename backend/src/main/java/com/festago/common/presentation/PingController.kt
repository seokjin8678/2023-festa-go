package com.festago.common.presentation

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/ping")
class PingController {

    @GetMapping
    fun ping(): String {
        return "pong"
    }
}
