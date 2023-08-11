package com.festago.auth.presentation;

import com.festago.auth.application.AuthService;
import com.festago.auth.domain.Login;
import com.festago.auth.dto.LoginMember;
import com.festago.auth.dto.LoginRequest;
import com.festago.auth.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/oauth2")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok()
            .body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@Login LoginMember loginMember) {
        authService.deleteMember(loginMember.memberId());
        return ResponseEntity.ok()
            .build();
    }
}
