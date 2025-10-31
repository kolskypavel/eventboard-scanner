package cz.eventboard.eventboard_scanner.db

/**
 * Simple holder for a platform-specific DatabaseDriverFactory instance.
 * Set this from the platform entrypoint (Activity/App delegate) so common code
 * can obtain a driver factory when needed.
 */
object DatabaseFactoryProvider {
    // Will be initialized on app start from MainActivity (Android) or platform entry.
    lateinit var factory: DatabaseDriverFactory
}

