package cz.eventboard.eventboard_scanner.db.entity

class Event {
    var name: String = ""
    var id: Long = 0
    var tickets : List<Ticket> = emptyList()

}