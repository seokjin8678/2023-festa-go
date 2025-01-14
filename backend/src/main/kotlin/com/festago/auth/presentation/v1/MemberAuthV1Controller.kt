package com.festago.auth.presentation.v1

import com.festago.auth.annotation.Authorization
import com.festago.auth.annotation.MemberAuth
import com.festago.auth.application.command.MemberAuthFacadeService
import com.festago.auth.domain.Role
import com.festago.auth.domain.SocialType
import com.festago.auth.domain.authentication.Authentication
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.auth.dto.v1.LoginV1Response
import com.festago.auth.dto.v1.LogoutV1Request
import com.festago.auth.dto.v1.OAuth2LoginV1Request
import com.festago.auth.dto.v1.OpenIdLoginV1Request
import com.festago.auth.dto.v1.RefreshTokenV1Request
import com.festago.auth.dto.v1.TokenRefreshV1Response
import com.festago.common.annotation.LoggingDetail
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "회원 인증 요청 V1")
class MemberAuthV1Controller(
    private val memberAuthFacadeService: MemberAuthFacadeService,
) {

    @LoggingDetail(hideRequestBody = true)
    @PostMapping("/login/oauth2")
    @Operation(
        description = "OAuth2 authorization_code를 받아 로그인/회원가입을 한다.",
        summary = "OAuth2 Authorization Code Grant 로그인"
    )
    fun oauth2Login(
        @Valid @RequestBody request: OAuth2LoginV1Request,
    ): ResponseEntity<LoginV1Response> {
        return ResponseEntity.ok()
            .body(memberAuthFacadeService.oAuth2Login(request.socialType, request.code))
    }

    @LoggingDetail(hideRequestBody = true)
    @Hidden // OAuth2 redirect-uri 스펙을 맞추기 위해 구현한 API
    @GetMapping("/login/oauth2/{socialType}")
    fun oauth2LoginWithPath(
        @PathVariable socialType: SocialType,
        @RequestParam code: String,
    ): ResponseEntity<LoginV1Response> {
        return ResponseEntity.ok()
            .body(memberAuthFacadeService.oAuth2Login(socialType, code))
    }

    @LoggingDetail(hideRequestBody = true)
    @PostMapping("/login/open-id")
    @Operation(description = "OpenID Id Token을 받아 로그인/회원가입을 한다.", summary = "OpenID Id Token 로그인")
    fun openIdLogin(
        @Valid @RequestBody request: OpenIdLoginV1Request,
    ): ResponseEntity<LoginV1Response> {
        return ResponseEntity.ok()
            .body(memberAuthFacadeService.openIdLogin(request.socialType, request.idToken))
    }

    @Authorization([Role.MEMBER, Role.ANONYMOUS])
    @PostMapping("/logout")
    @Operation(description = "로그인 된 사용자를 로그아웃 처리한다.", summary = "로그아웃")
    fun logout(
        authentication: Authentication,
        @Valid @RequestBody request: LogoutV1Request,
    ): ResponseEntity<Void> {
        if (authentication is MemberAuthentication) {
            memberAuthFacadeService.logout(authentication.memberId, UUID.fromString(request.refreshToken))
        }
        return ResponseEntity.ok().build()
    }

    @PostMapping("/refresh")
    @Operation(description = "액세스/리프래쉬 토큰을 재발급한다.", summary = "액세스/리프래쉬 토큰 재발급")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenV1Request,
    ): ResponseEntity<TokenRefreshV1Response> {
        return ResponseEntity.ok()
            .body(memberAuthFacadeService.refresh(UUID.fromString(request.refreshToken)))
    }

    @MemberAuth
    @DeleteMapping
    @Operation(description = "사용자를 탈퇴 처리한다.", summary = "회원 탈퇴")
    fun deleteAccount(memberAuthentication: MemberAuthentication): ResponseEntity<Void> {
        memberAuthFacadeService.deleteAccount(memberAuthentication.memberId)
        return ResponseEntity.ok().build()
    }
}
