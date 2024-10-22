package com.festago.admin.presentation

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.InternalServerException
import io.swagger.v3.oas.annotations.Hidden
import java.time.LocalDateTime
import java.time.ZoneId
import org.springframework.boot.info.BuildProperties
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api")
class AdminController(
    private val properties: BuildProperties?,
) {

    @GetMapping("/version")
    fun getVersion(): ResponseEntity<String> {
        return properties?.let { ResponseEntity.ok(it.time.atZone(ZoneId.of("Asia/Seoul")).toString()) }
            ?: ResponseEntity.ok().body(LocalDateTime.now().toString())
    }

    @GetMapping("/error")
    fun error(): ResponseEntity<Void> {
        throw IllegalArgumentException("테스트용 에러입니다.")
    }

    @GetMapping("/warn")
    fun warn(): ResponseEntity<Void> {
        throw InternalServerException(ErrorCode.FOR_TEST_ERROR)
    }

    @GetMapping("/info")
    fun info(): ResponseEntity<Void> {
        throw BadRequestException(ErrorCode.FOR_TEST_ERROR)
    }
}
