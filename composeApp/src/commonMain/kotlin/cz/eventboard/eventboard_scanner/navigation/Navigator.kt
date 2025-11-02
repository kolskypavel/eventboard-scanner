package cz.eventboard.eventboard_scanner.navigation

/**
 * Simple in-memory back dispatcher for Compose multiplatform.
 * Components can register a lambda to handle back requests. The last registered
 * handler that returns true claims the event.
 */
object BackDispatcher {
    private val handlers = mutableListOf<() -> Boolean>()

    fun register(handler: () -> Boolean) {
        handlers.add(handler)
    }

    fun unregister(handler: () -> Boolean) {
        handlers.remove(handler)
    }

    /**
     * Called by platform back-press. Returns true if handled.
     */
    fun handleBack(): Boolean {
        for (i in handlers.indices.reversed()) {
            val handler = handlers[i]
            if (handler()) return true
        }
        return false
    }
}
