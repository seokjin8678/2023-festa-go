package com.festago.festival.domain.validator

import com.festago.festival.domain.Festival

interface FestivalUpdateValidator {
    fun validate(festival: Festival)
}
