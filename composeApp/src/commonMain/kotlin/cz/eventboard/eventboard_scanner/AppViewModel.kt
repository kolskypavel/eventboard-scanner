package cz.eventboard.eventboard_scanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cz.eventboard.eventboard_scanner.business.EventService
import cz.eventboard.eventboard_scanner.business.TicketService
import cz.eventboard.eventboard_scanner.db.entity.Event
import cz.eventboard.eventboard_scanner.db.entity.Ticket
import cz.eventboard.eventboard_scanner.db.entity.TicketStatus

/**
 * Simple, platform-agnostic application state holder for the UI.
 * Keeps a list of tickets and other small pieces of app state as Compose MutableState so
 * composables can observe changes.
 * @author Pavel Kolsky
 */
class AppViewModel : ViewModel() {

    var loggedIn by mutableStateOf(false)
        private set

    var event by mutableStateOf<Event?>(null)
        private set

    // Tickets list observed by the UI
    var tickets by mutableStateOf(listOf<Ticket>())
        private set

    // Last checked ticket info (for status screen)
    var lastCheckedStatus by mutableStateOf<TicketStatus?>(null)
        private set

    var lastCheckedCode by mutableStateOf<String?>(null)
        private set

    val checkedCount: Int
        get() = tickets.count { it.status == TicketStatus.CHECKED }

    val ticketService = TicketService()
    val eventService = EventService()

    fun getEventByCode(eventCode: Int): Boolean {
        val ev = eventService.getEventByCode(eventCode)

        if (ev != null) {
            loggedIn = true
            return true
        }
        return false
    }

    fun validateTicket(id: Long): TicketStatus = ticketService.validateTicket(id, tickets)
    fun checkInTicket(id: Long): Boolean = ticketService.checkInTicket(id, tickets)

    fun setLastChecked(status: TicketStatus, code: String?) {
        lastCheckedStatus = status
        lastCheckedCode = code
    }

    fun clearLastChecked() {
        lastCheckedStatus = null
        lastCheckedCode = null
    }

    // helper to add sample data (useful for previews)
    fun populateSampleData() {
        tickets = listOf(
            Ticket(
                eventId = 1L,
                id = 145155,
                owner = "Alice Novak",
                status = TicketStatus.CHECKED,
                lastCheck = null,
                sector = null,
                row = null,
                seat = null
            ),
            Ticket(
                eventId = 1L,
                id = 254455,
                owner = "Bob Černý",
                status = TicketStatus.VALID,
                lastCheck = null,
                sector = null,
                row = null,
                seat = null
            ),
            Ticket(
                eventId = 1L,
                id = 2150023180376,
                owner = "Carmen Ruiz",
                status = TicketStatus.VALID,
                lastCheck = null,
                sector = null,
                row = null,
                seat = null
            )
        )
    }
}