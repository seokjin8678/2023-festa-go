package com.festago.school.domain

import com.festago.common.domain.BaseTimeEntity
import com.festago.common.util.Validator.maxLength
import com.festago.common.util.Validator.notBlank
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class School(
    id: Long?,
    domain: String,
    name: String,
    logoUrl: String?,
    backgroundImageUrl: String?,
    region: SchoolRegion,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    var domain: String = domain
        protected set

    @Column(unique = true)
    var name: String = name
        protected set

    var logoUrl: String = logoUrl?.ifBlank { "" } ?: ""
        protected set

    @Column(name = "background_image_url")
    var backgroundUrl: String = backgroundImageUrl?.ifBlank { "" } ?: ""
        protected set

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    var region: SchoolRegion = region
        protected set

    constructor(
        domain: String,
        name: String,
        logoUrl: String?,
        backgroundUrl: String?,
        region: SchoolRegion,
    ) : this(
        id = null,
        domain = domain,
        name = name,
        logoUrl = logoUrl,
        backgroundImageUrl = backgroundUrl,
        region = region
    )

    init {
        validateDomain(domain)
        validateName(name)
        validateImageUrl(logoUrl, "logoUrl")
        validateImageUrl(backgroundImageUrl, "backgroundImageUrl")
    }

    private fun validateDomain(domain: String) {
        val fieldName = "domain"
        notBlank(domain, fieldName)
        maxLength(domain, MAX_DOMAIN_LENGTH, fieldName)
    }

    private fun validateName(name: String) {
        val fieldName = "name"
        notBlank(name, fieldName)
        maxLength(name, MAX_NAME_LENGTH, fieldName)
    }

    private fun validateImageUrl(imageUrl: String?, fieldName: String) {
        maxLength(imageUrl, MAX_IMAGE_URL_LENGTH, fieldName)
    }

    fun changeDomain(domain: String) {
        validateDomain(domain)
        this.domain = domain
    }

    fun changeName(name: String) {
        validateName(name)
        this.name = name
    }

    fun changeRegion(region: SchoolRegion) {
        this.region = region
    }

    fun changeLogoUrl(logoUrl: String?) {
        validateImageUrl(logoUrl, "logoUrl")
        this.logoUrl = logoUrl?.ifBlank { "" } ?: ""
    }

    fun changeBackgroundImageUrl(backgroundImageUrl: String?) {
        validateImageUrl(backgroundImageUrl, "backgroundImageUrl")
        this.backgroundUrl = backgroundImageUrl?.ifBlank { "" } ?: ""
    }

    val identifier: Long
        get() = id!!

    companion object {
        private const val MAX_DOMAIN_LENGTH = 50
        private const val MAX_NAME_LENGTH = 255
        private const val MAX_IMAGE_URL_LENGTH = 255
    }
}
