package com.festago.auth.presentation.v1

import com.festago.auth.application.command.AdminAuthCommandService
import com.festago.auth.domain.AuthType
import com.festago.auth.domain.Role
import com.festago.auth.dto.command.AdminLoginResult
import com.festago.support.jsonPost
import com.festago.support.spec.ControllerDescribeSpec
import com.festago.support.withMockAuthExtension
import io.mockk.every
import jakarta.servlet.http.Cookie
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

private val TOKEN_COOKIE = Cookie("token", "token")

class AdminAuthV1ControllerTest(
    val mockMvc: MockMvc,
    val adminAuthCommandService: AdminAuthCommandService,
) : ControllerDescribeSpec({

    describe("어드민 로그인") {

        val uri = "/admin/api/v1/auth/login"

        context("POST $uri") {

            every { adminAuthCommandService.login(any()) } returns AdminLoginResult("admin", AuthType.ROOT, "token")

            it("200 응답과 로그인 토큰이_담긴 쿠키가 반환된다") {
                mockMvc.jsonPost(uri) {
                    content = """
                        {
                            "username": "admin",
                            "password": "1234"
                        }
                    """
                }.andExpect {
                    status { isOk() }
                    cookie {
                        exists(TOKEN_COOKIE.name)
                        path(TOKEN_COOKIE.name, "/")
                        secure(TOKEN_COOKIE.name, true)
                        httpOnly(TOKEN_COOKIE.name, true)
                        sameSite(TOKEN_COOKIE.name, "None")
                    }
                }
            }
        }
    }

    describe("어드민 로그아웃") {

        val uri = "/admin/api/v1/auth/logout"

        context("GET $uri") {

            it("200 응답과 비어있는 값의 로그인 토큰이 담긴 쿠키가 반환된다").config(
                extensions = withMockAuthExtension(role = Role.ADMIN)
            ) {

                mockMvc.get(uri) {
                    cookie(TOKEN_COOKIE)
                }.andExpect {
                    status { isOk() }
                    cookie {
                        exists(TOKEN_COOKIE.name)
                        value(TOKEN_COOKIE.name, "")
                        path(TOKEN_COOKIE.name, "/")
                        secure(TOKEN_COOKIE.name, true)
                        httpOnly(TOKEN_COOKIE.name, true)
                        sameSite(TOKEN_COOKIE.name, "None")
                    }
                }
            }

            it("토큰 없이 보내면 401 응답이 반환된다") {
                mockMvc.get(uri).andExpect {
                    status { isUnauthorized() }
                }
            }

            it("토큰의 권한이 Admin이 아니면 404 응답이 반환된다").config(
                extensions = withMockAuthExtension(role = Role.MEMBER)
            ) {
                mockMvc.get(uri) {
                    cookie(TOKEN_COOKIE)
                }.andExpect {
                    status { isNotFound() }
                }
            }
        }
    }

    describe("어드민 회원가입") {

        val uri = "/admin/api/v1/auth/signup"

        context("POST $uri") {

            it("200 응답과 생성한 계정이 반환된다").config(
                extensions = withMockAuthExtension(role = Role.ADMIN)
            ) {
                mockMvc.jsonPost(uri) {
                    cookie(TOKEN_COOKIE)
                    content = """
                        {
                            "username": "newAdmin",
                            "password": "1234"
                        }
                    """
                }.andExpect {
                    status { isOk() }
                }
            }

            it("토큰 없이 보내면 401 응답이 반환된다") {
                mockMvc.jsonPost(uri).andExpect {
                    status { isUnauthorized() }
                }
            }

            it("토큰의 권한이 Admin이 아니면 404 응답이 반환된다").config(
                extensions = withMockAuthExtension(role = Role.MEMBER)
            ) {
                mockMvc.jsonPost(uri) {
                    cookie(TOKEN_COOKIE)
                }.andExpect {
                    status { isNotFound() }
                }
            }
        }
    }

    describe("루트 어드민 활성화") {
        val uri = "/admin/api/v1/auth/initialize"

        context("POST $uri") {

            it("200 응답이 반환된다") {
                mockMvc.jsonPost(uri) {
                    content = """
                        {
                            "password": "1234"
                        }
                    """
                }.andExpect {
                    status { isOk() }
                }
            }
        }
    }
})
