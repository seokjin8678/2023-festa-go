package com.festago.festival.domain

import com.festago.common.domain.BaseTimeEntity
import com.festago.common.util.ImageUrlHelper
import com.festago.common.util.Validator
import com.festago.school.domain.School
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Festival(
    id: Long?,
    name: String,
    festivalDuration: FestivalDuration,
    posterImageUrl: String,
    school: School,
) : BaseTimeEntity() {

    constructor(
        name: String,
        festivalDuration: FestivalDuration,
        posterImageUrl: String,
        school: School,
    ) : this(null, name, festivalDuration, posterImageUrl, school)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    @Column(nullable = false)
    var name: String = name
        protected set

    @Embedded
    var festivalDuration: FestivalDuration = festivalDuration
        protected set

    val startDate get() = festivalDuration.startDate

    val endDate get() = festivalDuration.endDate

    @Column(nullable = false)
    var posterImageUrl: String = posterImageUrl
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "school_id")
    var school: School = school
        protected set

    init {
        validateName(name)
        validatePosterImageUrl(posterImageUrl)
    }

    private fun validateName(name: String) {
        val fieldName = "name"
        Validator.notBlank(name, fieldName)
        Validator.maxLength(name, MAX_NAME_LENGTH, fieldName)
    }

    private fun validatePosterImageUrl(posterImageUrl: String) {
        Validator.maxLength(posterImageUrl, MAX_POSTER_IMAGE_URL_LENGTH, "posterImageUrl")
    }

    fun isStartDateBeforeTo(date: LocalDate): Boolean {
        return festivalDuration.isStartDateBeforeTo(date)
    }

    fun isNotInDuration(dateTime: LocalDateTime): Boolean {
        return festivalDuration.isNotInDuration(dateTime.toLocalDate())
    }

    fun changeName(name: String) {
        validateName(name)
        this.name = name
    }

    fun changePosterImageUrl(posterImageUrl: String) {
        validatePosterImageUrl(posterImageUrl)
        this.posterImageUrl = ImageUrlHelper.getBlankStringIfBlank(posterImageUrl)
    }

    fun changeFestivalDuration(festivalDuration: FestivalDuration) {
        this.festivalDuration = festivalDuration
    }

    companion object {

        private const val MAX_NAME_LENGTH: Int = 50
        private const val MAX_POSTER_IMAGE_URL_LENGTH: Int = 255
    }
}