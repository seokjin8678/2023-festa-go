package com.festago.support.spec

import com.festago.auth.AuthenticateContext
import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.common.aop.ValidPageableAspect
import com.festago.support.TestAuthConfig
import com.festago.support.WithMockAuthExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import io.mockk.mockkClass
import org.junit.platform.commons.util.ClassFilter
import org.junit.platform.commons.util.ReflectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service

@Import(
    TestAuthConfig::class,
    MockkServiceBeanFactoryPostProcessor::class,
    ValidPageableAspect::class
)
@WebMvcTest
@EnableAspectJAutoProxy
abstract class ControllerDescribeSpec(body: DescribeSpec.() -> Unit = {}) : DescribeSpec(body) {

    override fun extensions() = listOf(SpringExtension)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    @Autowired
    lateinit var applicationContext: ApplicationContext

    override suspend fun beforeEach(testCase: TestCase) {
        val extension = testCase.config.extensions.filterIsInstance<WithMockAuthExtension>().firstOrNull()
            ?: WithMockAuthExtension(role = Role.ANONYMOUS)
        extension.apply {
            val authenticateContext = applicationContext.getBean(AuthenticateContext::class.java)
            val authentication = when (role) {
                Role.ANONYMOUS -> AnonymousAuthentication.getInstance()
                Role.MEMBER -> MemberAuthentication(id)
                Role.ADMIN -> AdminAuthentication(id)
            }
            authenticateContext.authentication = authentication
        }
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
        val authenticateContext = applicationContext.getBean(AuthenticateContext::class.java)
        authenticateContext.authentication = AnonymousAuthentication.getInstance()
    }
}

class MockkServiceBeanFactoryPostProcessor : BeanFactoryPostProcessor {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val classFilter = ClassFilter.of { it.isAnnotationPresent(Service::class.java) }
        ReflectionUtils.findAllClassesInPackage("com.festago", classFilter).forEach {
            beanFactory.registerSingleton(it.simpleName, mockkClass(it.kotlin))
        }
    }
}