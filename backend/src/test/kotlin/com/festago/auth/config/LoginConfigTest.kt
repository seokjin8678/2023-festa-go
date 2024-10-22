package com.festago.auth.config

import com.festago.auth.annotation.MemberAuth
import com.festago.auth.domain.Role
import com.festago.common.exception.ErrorCode
import com.festago.support.CustomWebMvcTest
import com.festago.support.WithMockAuth
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@CustomWebMvcTest
internal class LoginConfigTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Nested
    inner class MemberAuth_어노테이션이_붙은_핸들러_메서드는_인증_기능이_수행된다 {

        @Test
        @WithMockAuth(role = Role.ANONYMOUS)
        @Throws(Exception::class)
        fun 토큰이_없으면_401_응답이_반환된다() {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/annotation-member-auth"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        }

        @Test
        @WithMockAuth
        @Throws(Exception::class)
        fun 토큰이_있으면_200_응답이_반환된다() {
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/annotation-member-auth")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            )
                .andExpect(MockMvcResultMatchers.status().isOk())
        }

        @Test
        @WithMockAuth(role = Role.ADMIN)
        @Throws(Exception::class)
        fun 토큰의_권한이_어드민이면_404_응답이_반환된다() {
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/annotation-member-auth")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.NOT_ENOUGH_PERMISSION.name))
        }
    }
}

@RestController
private class AnnotationMemberAuthController {

    @MemberAuth
    @GetMapping("/api/annotation-member-auth")
    fun testAuthHandler(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }
}
