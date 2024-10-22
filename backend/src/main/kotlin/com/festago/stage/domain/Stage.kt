package com.festago.stage.domain

import com.festago.common.domain.BaseTimeEntity
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.util.Validator.notNull
import com.festago.festival.domain.Festival
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class Stage(
    id: Long?,
    startTime: LocalDateTime,
    ticketOpenTime: LocalDateTime,
    festival: Festival,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = id

    var startTime: LocalDateTime = startTime
        protected set

    var ticketOpenTime: LocalDateTime = ticketOpenTime
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    var festival: Festival = festival
        protected set

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "stageId",
        orphanRemoval = true,
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE]
    )
    var artists: MutableList<StageArtist> = ArrayList()
        protected set

    constructor(
        startTime: LocalDateTime,
        ticketOpenTime: LocalDateTime,
        festival: Festival,
    ) : this(
        id = null,
        startTime = startTime,
        ticketOpenTime = ticketOpenTime,
        festival = festival
    )

    init {
        validateFestival(festival)
        validateTime(startTime, ticketOpenTime, festival)
    }

    private fun validateFestival(festival: Festival) {
        notNull(festival, "festival")
    }

    private fun validateTime(startTime: LocalDateTime, ticketOpenTime: LocalDateTime, festival: Festival) {
        notNull(startTime, "startTime")
        notNull(ticketOpenTime, "ticketOpenTime")
        if (ticketOpenTime.isAfter(startTime) || ticketOpenTime.isEqual(startTime)) {
            throw BadRequestException(ErrorCode.INVALID_TICKET_OPEN_TIME)
        }
        if (festival.isNotInDuration(startTime)) {
            throw BadRequestException(ErrorCode.INVALID_STAGE_START_TIME)
        }
    }

    fun changeTime(startTime: LocalDateTime, ticketOpenTime: LocalDateTime) {
        validateTime(startTime, ticketOpenTime, festival)
        this.startTime = startTime
        this.ticketOpenTime = ticketOpenTime
    }

    fun renewArtists(artistIds: List<Long>) {
        artists.removeIf { !artistIds.contains(it.artistId) }
        val existsArtistIds = artists.map { it.artistId }.toHashSet()
        for (artistId in artistIds) {
            if (!existsArtistIds.contains(artistId)) {
                artists.add(StageArtist(this.identifier, artistId))
            }
        }
    }

    val artistIds: List<Long>
        get() = artists.map { it.artistId }

    val identifier: Long
        get() = id!!
}
