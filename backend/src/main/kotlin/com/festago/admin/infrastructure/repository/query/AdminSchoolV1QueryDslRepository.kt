package com.festago.admin.infrastructure.repository.query

import com.festago.admin.dto.school.AdminSchoolV1Response
import com.festago.admin.dto.school.QAdminSchoolV1Response
import com.festago.common.querydsl.QueryDslRepositorySupport
import com.festago.common.querydsl.SearchCondition
import com.festago.school.domain.QSchool.school
import com.festago.school.domain.School
import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils

@Repository
class AdminSchoolV1QueryDslRepository : QueryDslRepositorySupport(School::class.java) {

    fun findAll(searchCondition: SearchCondition): Page<AdminSchoolV1Response> {
        val pageable = searchCondition.pageable
        val searchFilter = searchCondition.searchFilter
        val searchKeyword = searchCondition.searchKeyword
        return applyPagination(pageable,
            {
                it.select(
                    QAdminSchoolV1Response(
                        school.id,
                        school.domain,
                        school.name,
                        school.region,
                        school.logoUrl,
                        school.backgroundUrl,
                        school.createdAt,
                        school.updatedAt
                    )
                )
                    .from(school)
                    .where(containSearchFilter(searchFilter, searchKeyword))
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
            },
            {
                it.select(school.count())
                    .where(containSearchFilter(searchFilter, searchKeyword))
                    .from(school)
            }
        )
    }

    private fun containSearchFilter(searchFilter: String, searchKeyword: String): BooleanExpression? {
        return when (searchFilter) {
            "id" -> eqId(searchKeyword)
            "domain" -> containsDomain(searchKeyword)
            "name" -> containsName(searchKeyword)
            "region" -> eqRegion(searchKeyword)
            else -> null
        }
    }

    private fun eqId(id: String): BooleanExpression? {
        if (StringUtils.hasText(id)) {
            return school.id.eq(id.toLong())
        }
        return null
    }

    private fun containsDomain(domain: String): BooleanExpression? {
        if (StringUtils.hasText(domain)) {
            return school.domain.contains(domain)
        }
        return null
    }

    private fun containsName(name: String): BooleanExpression? {
        if (StringUtils.hasText(name)) {
            return school.name.contains(name)
        }
        return null
    }

    private fun eqRegion(region: String): BooleanExpression? {
        if (StringUtils.hasText(region)) {
            return school.region.stringValue().eq(region)
        }
        return null
    }

    fun findById(schoolId: Long): AdminSchoolV1Response? {
        return fetchOne {
            it.select(
                QAdminSchoolV1Response(
                    school.id,
                    school.domain,
                    school.name,
                    school.region,
                    school.logoUrl,
                    school.backgroundUrl,
                    school.createdAt,
                    school.updatedAt
                )
            )
                .from(school)
                .where(school.id.eq(schoolId))
        }
    }
}
