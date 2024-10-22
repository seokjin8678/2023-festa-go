package com.festago.bookmark.domain

import com.festago.common.domain.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Bookmark(
    id: Long?,
    bookmarkType: BookmarkType,
    resourceId: Long,
    memberId: Long,
) : BaseTimeEntity() {

    constructor(
        bookmarkType: BookmarkType,
        resourceId: Long,
        memberId: Long,
    ) : this(null, bookmarkType, resourceId, memberId)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    @Enumerated(EnumType.STRING)
    @Column(name = "bookmark_type", columnDefinition = "varchar")
    var bookmarkType: BookmarkType = bookmarkType
        protected set

    var resourceId: Long = resourceId
        protected set

    var memberId: Long = memberId
        protected set
}
