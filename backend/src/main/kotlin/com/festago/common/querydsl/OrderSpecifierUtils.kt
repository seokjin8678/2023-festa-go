package com.festago.common.querydsl

import com.querydsl.core.types.Expression
import com.querydsl.core.types.NullExpression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import org.springframework.data.domain.Sort

object OrderSpecifierUtils {

    val NULL = OrderSpecifier(
        Order.ASC,
        NullExpression.DEFAULT as NullExpression<out Comparable<*>>,
        OrderSpecifier.NullHandling.Default
    )

    fun of(direction: Sort.Direction, target: Expression<out Comparable<*>>): OrderSpecifier<*> {
        return OrderSpecifier(if (direction.isAscending) Order.ASC else Order.DESC, target)
    }
}
