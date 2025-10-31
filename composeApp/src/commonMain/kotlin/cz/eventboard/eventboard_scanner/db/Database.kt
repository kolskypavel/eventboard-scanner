package cz.eventboard.eventboard_scanner.db

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = EventBoardDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.eventBoardDatabaseQueries
}