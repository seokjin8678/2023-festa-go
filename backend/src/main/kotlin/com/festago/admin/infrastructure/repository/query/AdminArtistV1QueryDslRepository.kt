package com.festago.admin.infrastructure.repository.query

import com.festago.admin.dto.artist.AdminArtistV1Response
import com.festago.admin.dto.artist.QAdminArtistV1Response
import com.festago.artist.domain.Artist
import com.festago.artist.domain.QArtist.artist
import com.festago.common.querydsl.QueryDslRepositorySupport
import com.festago.common.querydsl.SearchCondition
import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils

@Repository
class AdminArtistV1QueryDslRepository : QueryDslRepositorySupport(Artist::class.java) {

    fun findAll(searchCondition: SearchCondition): Page<AdminArtistV1Response> {
        val pageable = searchCondition.pageable
        return applyPagination(pageable,
            {
                it.select(
                    QAdminArtistV1Response(
                        artist.id,
                        artist.name,
                        artist.profileImage,
                        artist.backgroundImageUrl,
                        artist.createdAt,
                        artist.updatedAt
                    )
                )
                    .from(artist)
                    .where(applySearch(searchCondition))
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
            },
            {
                it.select(artist.count())
                    .where(applySearch(searchCondition))
                    .from(artist)
            })
    }

    private fun applySearch(searchCondition: SearchCondition): BooleanExpression? {
        val searchFilter = searchCondition.searchFilter
        val searchKeyword = searchCondition.searchKeyword
        return when (searchFilter) {
            "id" -> eqId(searchKeyword)
            "name" -> if (searchKeyword.length == 1) eqName(searchKeyword) else containsName(searchKeyword)
            else -> null
        }
    }

    private fun eqId(id: String): BooleanExpression? {
        if (StringUtils.hasText(id)) {
            return artist.id.eq(id.toLong())
        }
        return null
    }

    private fun eqName(name: String): BooleanExpression? {
        if (StringUtils.hasText(name)) {
            return artist.name.eq(name)
        }
        return null
    }

    private fun containsName(name: String): BooleanExpression? {
        if (StringUtils.hasText(name)) {
            return artist.name.contains(name)
        }
        return null
    }

    fun findById(artistId: Long): AdminArtistV1Response? {
        return fetchOne {
            it.select(
                QAdminArtistV1Response(
                    artist.id,
                    artist.name,
                    artist.profileImage,
                    artist.backgroundImageUrl,
                    artist.createdAt,
                    artist.updatedAt
                )
            )
                .from(artist)
                .where(artist.id.eq(artistId))
        }
    }
}
