package cz.eventboard.eventboard_scanner.business

import cz.eventboard.eventboard_scanner.db.entity.Ticket
import cz.eventboard.eventboard_scanner.db.entity.TicketStatus

class TicketService {

    fun validateTicket(id: Long, tickets: List<Ticket>): TicketStatus {
        val ticket = tickets.find { it.id == id }
        return ticket?.status ?: TicketStatus.INVALID
    }

    fun checkInTicket(id: Long, tickets: List<Ticket>): Boolean {
        val ticket = tickets.find { it.id == id }
        if (ticket != null) {
            if (ticket.status == TicketStatus.VALID) {
                ticket.status = TicketStatus.CHECKED

                //TODO: finish
                // ticket.lastCheck= LocalDateTime.now()

                // trigger recomposition
                // tickets = tickets.toList()
                return true
            }
        }
        return false
    }
}