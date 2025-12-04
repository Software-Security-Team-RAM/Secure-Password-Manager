/**
* Created largely using Gemini Script
* Creates the local database and database access
*/

package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import Database.SafeByteDatabase
import java.io.File

object DatabaseDriverFactory {
    fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:safebyte.db")

        // Create the table if the file doesn't exist yet
        if (!File("safebyte.db").exists()) {
            SafeByteDatabase.Schema.create(driver)
        }
        return driver
    }
}