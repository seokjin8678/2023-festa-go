package com.festago.auth.domain

import com.festago.common.domain.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.domain.Persistable

@Entity
class RefreshToken(
    memberId: Long,
    expiredAt: LocalDateTime,
) : BaseTimeEntity(), Persistable<UUID> {

    @Id
    private val id: UUID = UUID.randomUUID()

    var memberId = memberId
        protected set

    var expiredAt = expiredAt
        protected set

    fun isExpired(now: LocalDateTime) = now > expiredAt

    fun isOwner(memberId: Long) = memberId == this.memberId

    override fun getId() = id

    override fun isNew() = createdAt == null

    companion object {
        private const val EXPIRED_OFFSET_DAY = 7L

        fun of(memberId: Long, now: LocalDateTime): RefreshToken {
            return RefreshToken(memberId, now.plusDays(EXPIRED_OFFSET_DAY))
        }
    }
}
