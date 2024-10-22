package com.festago.common.querydsl

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Expression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.function.Function
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component

@Component
class QueryDslHelper(
    private val queryFactory: JPAQueryFactory,
) {

    fun <T> select(expr: Expression<T>): JPAQuery<T> {
        return queryFactory.select(expr)
    }

    fun <T> selectFrom(expr: EntityPath<T>): JPAQuery<T> {
        return queryFactory.selectFrom(expr)
    }

    @Nullable
    fun <T> fetchOne(queryFunction: Function<JPAQueryFactory, JPAQuery<T>>): T? {
        return queryFunction.apply(queryFactory).fetchOne()
    }

    fun <T> applyPagination(
        pageable: Pageable,
        contentQueryFunction: Function<JPAQueryFactory, JPAQuery<T>>,
        countQueryFunction: Function<JPAQueryFactory, JPAQuery<Long>>,
    ): Page<T> {
        val content = contentQueryFunction.apply(queryFactory).fetch()
        val countQuery = countQueryFunction.apply(queryFactory)
        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne()!! }
    }

    fun <T> applySlice(pageable: Pageable, contentQueryFunction: Function<JPAQueryFactory, JPAQuery<T>>): Slice<T> {
        val content = contentQueryFunction.apply(queryFactory)
            .limit(pageable.pageSize + NEXT_PAGE_OFFSET)
            .fetch()
        if (content.size > pageable.pageSize) {
            content.removeLast()
            return SliceImpl(content, pageable, true)
        }
        return SliceImpl(content, pageable, false)
    }

    companion object {
        private const val NEXT_PAGE_OFFSET = 1L
    }
}
