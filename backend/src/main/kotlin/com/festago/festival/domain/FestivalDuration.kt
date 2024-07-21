package com.festago.festival.domain

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
data class FestivalDuration(
    @Column(nullable = false)
    val startDate: LocalDate,
    @Column(nullable = false)
    val endDate: LocalDate
) {

    init {
        if (startDate > endDate) {
            throw BadRequestException(ErrorCode.INVALID_FESTIVAL_DURATION)
        }
    }

    fun isStartDateBeforeTo(date: LocalDate): Boolean {
        return startDate < date
    }

    fun isNotInDuration(date: LocalDate): Boolean {
        return date !in startDate..endDate
    }
}
