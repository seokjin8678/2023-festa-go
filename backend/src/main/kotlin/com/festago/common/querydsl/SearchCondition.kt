package com.festago.common.querydsl

import org.springframework.data.domain.Pageable

data class SearchCondition(
    val searchFilter: String,
    val searchKeyword: String,
    val pageable: Pageable,
) {

}
