package com.festago.support.spec

import com.festago.support.TestTimeConfigKt
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
@Import(TestTimeConfigKt::class)
abstract class IntegrationDescribeSpec(body: DescribeSpec.() -> Unit = {}) : DescribeSpec(body) {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    override fun extensions() = listOf(SpringExtension)

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
        withContext(Dispatchers.IO) {
            jdbcTemplate.query("SHOW TABLES") { rs, _ -> rs.getString(1) }.apply {
                jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = ?", 0)
                forEach { tableName -> jdbcTemplate.update("TRUNCATE TABLE $tableName") }
                jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = ?", 1)
            }
        }
    }

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf
}