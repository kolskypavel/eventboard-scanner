package cz.eventboard.eventboard_scanner.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(private val context: Context) : cz.eventboard.eventboard_scanner.db.DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(EventBoardDatabase.Schema, context, "launch.db")
    }
}