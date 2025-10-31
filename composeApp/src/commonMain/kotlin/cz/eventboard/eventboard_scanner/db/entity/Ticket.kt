package cz.eventboard.eventboard_scanner.db.entity

import kotlinx.datetime.LocalDateTime

class Ticket(
    val eventId: Long,
    val id: Long,
    var status: TicketStatus,
    var lastCheck: LocalDateTime?,

    // Not used yet
    val sector: String?,
    val row: String?,
    val seat: String?
)
