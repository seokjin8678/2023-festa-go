package com.festago.support

import com.festago.auth.domain.Role
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class WithMockAuthExtension(
    val id: Long = 1,
    val role: Role = Role.MEMBER,
) : TestCaseExtension {

    override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
        return execute(testCase)
    }
}

/**
 * listOf(WithMockAuthExtension())을 대신하여 사용하는 유틸 함수
 */
fun withMockAuthExtension(id: Long = 1, role: Role = Role.MEMBER): List<TestCaseExtension> {
    return listOf(WithMockAuthExtension(id, role))
}
