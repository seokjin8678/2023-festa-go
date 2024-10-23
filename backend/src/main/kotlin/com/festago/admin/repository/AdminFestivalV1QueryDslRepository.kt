package com.festago.admin.repository

import com.festago.admin.dto.festival.AdminFestivalV1Response
import com.festago.admin.dto.festival.QAdminFestivalV1Response
import com.festago.common.querydsl.OrderSpecifierUtils
import com.festago.common.querydsl.QueryDslHelper
import com.festago.common.querydsl.SearchCondition
import com.festago.festival.domain.QFestival.festival
import com.festago.school.domain.QSchool.school
import com.festago.stage.domain.QStage.stage
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils

@Repository
class AdminFestivalV1QueryDslRepository(
    private val queryDslHelper: QueryDslHelper,
) {

    fun findAll(searchCondition: SearchCondition): Page<AdminFestivalV1Response> {
        val pageable = searchCondition.pageable
        val searchFilter = searchCondition.searchFilter
        val searchKeyword = searchCondition.searchKeyword
        return queryDslHelper.applyPagination(pageable,
            {
                it.select(
                    QAdminFestivalV1Response(
                        festival.id,
                        festival.name,
                        school.name,
                        festival.festivalDuration.startDate,
                        festival.festivalDuration.endDate,
                        stage.count()
                    )
                )
                    .from(festival)
                    .innerJoin(school).on(school.id.eq(festival.school.id))
                    .leftJoin(stage).on(stage.festival.id.eq(festival.id))
                    .where(applySearchFilter(searchFilter, searchKeyword))
                    .groupBy(festival.id)
                    .orderBy(getOrderSpecifier(pageable.sort))
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
            },
            {
                it.select(festival.count())
                    .from(festival)
                    .where(applySearchFilter(searchFilter, searchKeyword))
            })
    }

    private fun applySearchFilter(searchFilter: String, searchKeyword: String): BooleanExpression? {
        return when (searchFilter) {
            "id" -> eqId(searchKeyword)
            "name" -> containsName(searchKeyword)
            "schoolName" -> containsSchoolName(searchKeyword)
            else -> null
        }
    }

    private fun eqId(searchKeyword: String): BooleanExpression? {
        if (StringUtils.hasText(searchKeyword)) {
            return festival.id.eq(searchKeyword.toLong())
        }
        return null
    }

    private fun containsName(searchKeyword: String): BooleanExpression? {
        if (StringUtils.hasText(searchKeyword)) {
            return festival.name.contains(searchKeyword)
        }
        return null
    }

    private fun containsSchoolName(searchKeyword: String): BooleanExpression? {
        if (StringUtils.hasText(searchKeyword)) {
            return school.name.contains(searchKeyword)
        }
        return null
    }

    private fun getOrderSpecifier(sort: Sort): OrderSpecifier<*> {
        return sort.firstOrNull()
            .let {
                when (it?.property) {
                    "id" -> OrderSpecifierUtils.of(it.direction, festival.id)
                    "name" -> OrderSpecifierUtils.of(it.direction, festival.name)
                    "schoolName" -> OrderSpecifierUtils.of(it.direction, school.name)
                    "startDate" -> OrderSpecifierUtils.of(it.direction, festival.festivalDuration.startDate)
                    "endDate" -> OrderSpecifierUtils.of(it.direction, festival.festivalDuration.endDate)
                    else -> OrderSpecifierUtils.NULL
                }
            }
    }
}
