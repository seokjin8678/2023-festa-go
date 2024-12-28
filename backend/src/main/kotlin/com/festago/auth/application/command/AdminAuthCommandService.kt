package com.festago.auth.application.command

import com.festago.admin.domain.Admin
import com.festago.admin.domain.AdminRepository
import com.festago.auth.domain.AuthType
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.domain.token.jwt.provider.AdminAuthenticationTokenProvider
import com.festago.auth.dto.command.AdminLoginCommand
import com.festago.auth.dto.command.AdminLoginResult
import com.festago.auth.dto.command.AdminSignupCommand
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminAuthCommandService(
    private val adminAuthenticationTokenProvider: AdminAuthenticationTokenProvider,
    private val adminRepository: AdminRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional(readOnly = true)
    fun login(command: AdminLoginCommand): AdminLoginResult {
        val admin = findAdminWithAuthenticate(command)
        val adminAuthentication = AdminAuthentication(admin.identifier)
        val accessToken = adminAuthenticationTokenProvider.provide(adminAuthentication).token
        return AdminLoginResult(
            username = admin.username,
            authType = getAuthType(admin),
            accessToken = accessToken
        )
    }

    private fun findAdminWithAuthenticate(request: AdminLoginCommand): Admin {
        return adminRepository.findByUsername(request.username)
            ?.takeIf { passwordEncoder.matches(request.password, it.password) }
            ?: throw UnauthorizedException(ErrorCode.INCORRECT_PASSWORD_OR_ACCOUNT)
    }

    private fun getAuthType(admin: Admin): AuthType {
        return if (admin.isRootAdmin) AuthType.ROOT else AuthType.ADMIN
    }

    fun signup(adminId: Long, command: AdminSignupCommand) {
        validateRootAdmin(adminId)
        val username = command.username
        val password = passwordEncoder.encode(command.password)
        validateExistsUsername(username)
        adminRepository.save(Admin(username, password))
    }

    private fun validateRootAdmin(adminId: Long) {
        val findAdmin = adminRepository.findById(adminId)
        if (findAdmin == null || !findAdmin.isRootAdmin) {
            throw ForbiddenException(ErrorCode.NOT_ENOUGH_PERMISSION)
        }
    }

    private fun validateExistsUsername(username: String) {
        if (adminRepository.existsByUsername(username)) {
            throw BadRequestException(ErrorCode.DUPLICATE_ACCOUNT_USERNAME)
        }
    }

    fun initializeRootAdmin(password: String) {
        if (adminRepository.existsByUsername(Admin.ROOT_ADMIN_NAME)) {
            throw BadRequestException(ErrorCode.DUPLICATE_ACCOUNT_USERNAME)
        }
        adminRepository.save(Admin.createRootAdmin(passwordEncoder.encode(password)))
    }
}
