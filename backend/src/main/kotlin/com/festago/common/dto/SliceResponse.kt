package com.festago.common.dto

import org.springframework.data.domain.Slice

data class SliceResponse<T>(
    val last: Boolean,
    val content: List<T>,
) {
    companion object {
        fun <T> from(slice: Slice<T>): SliceResponse<T> {
            return SliceResponse(slice.isLast, slice.content)
        }
    }
}
