package com.festago.socialmedia.domain

import com.festago.common.domain.BaseTimeEntity
import com.festago.common.util.Validator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "social_media",
    uniqueConstraints = [UniqueConstraint(columnNames = ["owner_id", "owner_type", "media_type"])]
)
class SocialMedia(
    id: Long?,
    ownerId: Long,
    ownerType: OwnerType,
    mediaType: SocialMediaType,
    name: String,
    logoUrl: String?,
    url: String,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    @Column(name = "owner_id")
    var ownerId: Long = ownerId
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", columnDefinition = "varchar")
    var ownerType: OwnerType = ownerType
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", columnDefinition = "varchar")
    var mediaType: SocialMediaType = mediaType
        protected set

    var name: String = name
        protected set

    var logoUrl: String = logoUrl ?: ""
        protected set

    var url: String = url
        protected set

    constructor(
        ownerId: Long,
        ownerType: OwnerType,
        mediaType: SocialMediaType,
        name: String,
        logoUrl: String?,
        url: String,
    ) : this(null, ownerId, ownerType, mediaType, name, logoUrl, url)

    init {
        Validator.notBlank(name, "name")
        Validator.notBlank(url, "url")
    }

    val identifier: Long
        get() = id!!

    fun changeName(name: String) {
        Validator.notBlank(name, "name")
        this.name = name
    }

    fun changeUrl(url: String) {
        Validator.notBlank(url, "url")
        this.url = url
    }

    fun changeLogoUrl(logoUrl: String?) {
        this.logoUrl = logoUrl ?: ""
    }
}
