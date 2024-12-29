package com.festago.auth.presentation.v1

import com.festago.auth.application.command.AdminAuthCommandService
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.dto.v1.AdminLoginV1Request
import com.festago.auth.dto.v1.AdminLoginV1Response
import com.festago.auth.dto.v1.AdminSignupV1Request
import com.festago.auth.dto.v1.RootAdminInitializeRequest
import com.festago.common.annotation.LoggingDetail
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import java.time.Duration
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/admin/api/v1/auth")
class AdminAuthV1Controller(
    private val adminAuthCommandService: AdminAuthCommandService,
) {

    @LoggingDetail(hideRequestBody = true)
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: AdminLoginV1Request,
    ): ResponseEntity<AdminLoginV1Response> {
        val result = adminAuthCommandService.login(request.toCommand())
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, createLoginCookie(result.accessToken))
            .body(AdminLoginV1Response(result.username, result.authType))
    }

    private fun createLoginCookie(token: String): String {
        return ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .build().toString()
    }

    @GetMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, createLogoutCookie())
            .build()
    }

    private fun createLogoutCookie(): String {
        return ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ZERO)
            .build().toString()
    }

    @LoggingDetail(hideRequestBody = true)
    @PostMapping("/signup")
    fun signupAdminAccount(
        @Valid @RequestBody request: AdminSignupV1Request,
        adminAuthentication: AdminAuthentication,
    ): ResponseEntity<Void> {
        adminAuthCommandService.signup(adminAuthentication.memberId, request.toCommand())
        return ResponseEntity.ok().build()
    }

    @LoggingDetail(hideRequestBody = true)
    @PostMapping("/initialize")
    fun initializeRootAdmin(
        @Valid @RequestBody request: RootAdminInitializeRequest,
    ): ResponseEntity<Void> {
        adminAuthCommandService.initializeRootAdmin(request.password)
        return ResponseEntity.ok().build()
    }
}
