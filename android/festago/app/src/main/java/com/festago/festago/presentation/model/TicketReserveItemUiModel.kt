package com.festago.festago.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TicketReserveItemUiModel(
    val id: Int,
    val lineUp: String,
    val startTime: LocalDateTime,
    val ticketOpenTime: LocalDateTime,
    val reservationTickets: List<ReservationTicketUiModel>,
    val canReserve: Boolean,
    val isSigned: Boolean,
) : Parcelable