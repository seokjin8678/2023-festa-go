package com.festago.auth.presentation.v1

import com.festago.support.jsonDelete
import com.festago.support.jsonGet
import com.festago.support.jsonPost
import com.festago.support.mockAuthHeader
import com.festago.support.spec.ControllerDescribeSpec
import com.festago.support.withMockAuthExtension
import java.util.UUID
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

class MemberAuthV1ControllerTest(
    val mockMvc: MockMvc,
) : ControllerDescribeSpec({

    describe("회원 로그인") {
        val uri = "/api/v1/auth/login/oauth2"

        context("POST $uri") {

            it("200 응답과 로그인 응답이 반환된다") {

                mockMvc.jsonPost(uri) {
                    content = """
                        {
                            "socialType": "FESTAGO",
                            "code": "code"
                        }
                    """
                }.andExpect {
                    status { isOk() }
                }
            }
        }
    }

    describe("회원 로그인 with PathVariable") {
        val uri = "/api/v1/auth/login/oauth2/{socialType}"

        context("GET $uri") {

            it("200 응답과 로그인 응답이 반환된다") {
                mockMvc.jsonGet(uri, "festago") {
                    queryParam("code", "1")
                }.andExpect {
                    status { isOk() }
                }
            }
        }
    }

    describe("OpenID 회원 로그인") {
        val uri = "/api/v1/auth/login/open-id"

        context("POST $uri") {

            it("200 응답과 로그인 응답이 반환된다") {
                mockMvc.jsonPost(uri) {
                    content = """
                        {
                            "socialType": "FESTAGO",
                            "idToken": "token"
                        }
                    """
                }.andExpect {
                    status { isOk() }
                }
            }
        }
    }

    describe("회원 로그아웃") {
        val uri = "/api/v1/auth/logout"

        context("POST $uri") {

            it("200 응답이 반환된다").config(
                extensions = withMockAuthExtension()
            ) {
                mockMvc.jsonPost(uri) {
                    content = """
                       {
                            "refreshToken": "${UUID.randomUUID()}"
                       }
                    """
                    mockAuthHeader()
                }.andExpect {
                    status { isOk() }
                }
            }

            it("토큰 없이 보내면 401 응답이 반환된다") {
                mockMvc.post(uri).andExpect {
                    status { isUnauthorized() }
                }
            }
        }
    }

    describe("토큰 리프래쉬") {
        val uri = "/api/v1/auth/refresh"

        context("POST $uri") {

            it("200 응답이 반환된다.") {
                mockMvc.jsonPost(uri) {
                    content = """
                        {
                            "refreshToken": "${UUID.randomUUID()}"
                        }
                    """.trimIndent()
                    mockAuthHeader()
                }.andExpect {
                    status { isOk() }
                }
            }
        }
    }

    describe("회원 탈퇴") {

        val uri = "/api/v1/auth"

        context("DELETE $uri") {

            it("200 응답이 반환된다").config(
                extensions = withMockAuthExtension()
            ) {
                mockMvc.jsonDelete(uri) {
                    mockAuthHeader()
                }.andExpect {
                    status { isOk() }
                }
            }

            it("토큰 없이 보내면 401 응답이 반환된다") {
                mockMvc.jsonDelete(uri).andExpect {
                    status { isUnauthorized() }
                }
            }
        }
    }
})
