package com.festago.auth.application.command

import com.festago.admin.domain.Admin
import com.festago.admin.domain.AdminRepository
import com.festago.auth.domain.AuthType
import com.festago.auth.domain.SocialType
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.domain.token.jwt.provider.AdminAuthenticationTokenProvider
import com.festago.auth.dto.command.AdminLoginCommand
import com.festago.auth.dto.command.AdminLoginResult
import com.festago.auth.dto.command.AdminSignupCommand
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import com.festago.member.domain.Member
import com.festago.member.domain.MemberRepository
import java.util.UUID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminAuthCommandService(
    private val adminAuthenticationTokenProvider: AdminAuthenticationTokenProvider,
    private val adminRepository: AdminRepository,
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun login(command: AdminLoginCommand): AdminLoginResult {
        val admin = findAdmin(command.username, command.password)
        val adminAuthentication = AdminAuthentication(admin.memberId)
        val accessToken = adminAuthenticationTokenProvider.provide(adminAuthentication).token
        return AdminLoginResult(
            username = admin.username,
            authType = getAuthType(admin),
            accessToken = accessToken
        )
    }

    private fun findAdmin(username: String, password: String): Admin {
        return adminRepository.findByUsername(username)
            ?.takeIf { passwordEncoder.matches(password, it.password) }
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
        val member = createAdminMember(username)
        adminRepository.save(
            Admin(
                memberId = member.identifier,
                username = username,
                password = password
            )
        )
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

    private fun createAdminMember(username: String): Member {
        return memberRepository.save(
            Member(
                socialId = UUID.randomUUID().toString(),
                socialType = SocialType.FESTAGO,
                nickname = username
            )
        )
    }

    fun initializeRootAdmin(password: String) {
        if (adminRepository.existsByUsername(Admin.ROOT_ADMIN_NAME)) {
            throw BadRequestException(ErrorCode.DUPLICATE_ACCOUNT_USERNAME)
        }
        val adminMember = createAdminMember(Admin.ROOT_ADMIN_NAME)
        adminRepository.save(
            Admin.createRootAdmin(
                memberId = adminMember.identifier,
                password = passwordEncoder.encode(password)
            )
        )
    }
}
