package com.festago.common.querydsl

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import java.util.function.Function
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.querydsl.SimpleEntityPathResolver
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.lang.Nullable
import org.springframework.util.Assert

abstract class QueryDslRepositorySupport(
    private val domainClass: Class<*>,
) {
    private lateinit var querydsl: Querydsl
    private lateinit var queryFactory: JPAQueryFactory

    @Autowired
    protected fun setQueryFactory(entityManager: EntityManager) {
        Assert.notNull(entityManager, "EntityManager must not be null!")
        val entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager)
        val resolver = SimpleEntityPathResolver.INSTANCE
        val path = resolver.createPath(entityInformation.javaType)
        this.querydsl = Querydsl(entityManager, PathBuilder(path.type, path.metadata))
        this.queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)
    }

    protected fun <T> select(expr: Expression<T>): JPAQuery<T> {
        return queryFactory.select(expr)
    }

    protected fun <T> selectFrom(expr: EntityPath<T>): JPAQuery<T> {
        return queryFactory.selectFrom(expr)
    }

    @Nullable
    protected fun <T> fetchOne(queryFunction: Function<JPAQueryFactory, JPAQuery<T>>): T? {
        return queryFunction.apply(queryFactory).fetchOne()
    }

    protected fun <T> applyPagination(
        pageable: Pageable,
        contentQueryFunction: Function<JPAQueryFactory, JPAQuery<T>>,
        countQueryFunction: Function<JPAQueryFactory, JPAQuery<Long>>,
    ): Page<T> {
        val contentQuery = contentQueryFunction.apply(queryFactory)
        val content = querydsl.applyPagination(pageable, contentQuery).fetch()
        val countQuery = countQueryFunction.apply(queryFactory)
        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne()!! }
    }

    protected fun <T> applySlice(
        pageable: Pageable,
        contentQueryFunction: Function<JPAQueryFactory, JPAQuery<T>>,
    ): Slice<T> {
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
