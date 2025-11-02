package cz.eventboard.eventboard_scanner.business

import cz.eventboard.eventboard_scanner.db.entity.Event

class EventService {

    fun getEventByCode(eventCode: Int): Event? {
        // Implement event retrieval logic here
        return getDummyEvent()
    }

    fun getDummyEvent(): Event {
        val event = Event()
         return event
    }
}