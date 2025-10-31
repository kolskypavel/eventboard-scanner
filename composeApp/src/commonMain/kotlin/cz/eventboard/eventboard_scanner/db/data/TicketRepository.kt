package cz.eventboard.eventboard_scanner.db.data

import cz.eventboard.eventboard_scanner.db.EventBoardDatabase
import cz.eventboard.eventboard_scanner.db.entity.Ticket

class TicketRepository(db: EventBoardDatabase) {
  //  private val queries = db.eventBoardDatabaseQueries

 //   fun getAllTickets(): List<Ticket> = queries.selectAll().executeAsList()

 //   fun getTicketByCode(code: String): Ticket? = queries.selectByCode(code).executeAsOneOrNull()

//    fun insertTicket(ticket: Ticket) {
        //  queries.insertTicket(ticket.id, ticket.code, ticket.eventId, ticket.checked, ticket.holderName)
  //  }

    fun setChecked(id: String, checked: Boolean) {
        //queries.updateChecked(if (checked) 1L else 0L, id)
    }

    //   fun countChecked(eventId: String): Long = queries.countCheckedByEvent(eventId).executeAsOne()
    //     .let { it }
    //fun countAll(eventId: String): Long = queries.countAllByEvent(eventId).executeAsOne()
    //  .let { it }
}
