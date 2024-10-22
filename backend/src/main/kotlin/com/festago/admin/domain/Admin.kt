package com.festago.admin.domain

import com.festago.common.domain.BaseTimeEntity
import com.festago.common.util.Validator.maxLength
import com.festago.common.util.Validator.minLength
import com.festago.common.util.Validator.notBlank
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Size

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "UNIQUE_USERNAME", columnNames = ["username"])])
class Admin(
    id: Long?,
    username: String,
    password: String,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
    var username: String = username
        protected set

    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    var password: String = password
        protected set

    constructor(
        username: String,
        password: String,
    ) : this(null, username, password)

    init {
        validateUsername(username)
        validatePassword(password)
    }

    private fun validateUsername(username: String) {
        val fieldName = "username"
        notBlank(username, fieldName)
        minLength(username, MIN_USERNAME_LENGTH, fieldName)
        maxLength(username, MAX_USERNAME_LENGTH, fieldName)
    }

    private fun validatePassword(password: String) {
        val fieldName = "password"
        notBlank(password, fieldName)
        minLength(password, MIN_PASSWORD_LENGTH, fieldName)
        maxLength(password, MAX_PASSWORD_LENGTH, fieldName)
    }

    val identifier: Long
        get() = id!!

    val isRootAdmin: Boolean
        get() = username == ROOT_ADMIN_NAME

    companion object {
        const val ROOT_ADMIN_NAME: String = "admin"
        private const val MIN_USERNAME_LENGTH = 4
        private const val MAX_USERNAME_LENGTH = 20
        private const val MIN_PASSWORD_LENGTH = 4
        private const val MAX_PASSWORD_LENGTH = 255

        fun createRootAdmin(password: String): Admin {
            return Admin(ROOT_ADMIN_NAME, password)
        }
    }
}
