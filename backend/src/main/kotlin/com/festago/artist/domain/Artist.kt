package com.festago.artist.domain

import com.festago.common.domain.BaseTimeEntity
import com.festago.common.util.Validator.notBlank
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.proxy.HibernateProxy

@Entity
class Artist(
    id: Long?,
    name: String,
    profileImage: String?,
    backgroundImageUrl: String?,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    var name: String = name
        protected set

    @Column(name = "profile_image_url")
    var profileImage: String = profileImage?.ifBlank { "" } ?: ""
        protected set

    var backgroundImageUrl: String = backgroundImageUrl?.ifBlank { "" } ?: ""
        protected set

    constructor(name: String, profileImage: String?, backgroundImageUrl: String?) : this(
        id = null,
        name = name,
        profileImage = profileImage,
        backgroundImageUrl = backgroundImageUrl
    )

    init {
        validateName(name)
    }

    private fun validateName(name: String) {
        notBlank(name, "name")
    }

    fun update(name: String, profileImage: String?, backgroundImageUrl: String?) {
        validateName(name)
        this.name = name
        this.profileImage = profileImage?.ifBlank { "" } ?: ""
        this.backgroundImageUrl = backgroundImageUrl?.ifBlank { "" } ?: ""
    }

    val identifier: Long
        get() = id!!

    fun copy(): Artist {
        return Artist(
            id = id,
            name = name,
            profileImage = profileImage,
            backgroundImageUrl = backgroundImageUrl
        )
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Artist
        return id != null && id == other.id
    }

    final override fun hashCode(): Int {
        return if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
    }
}
