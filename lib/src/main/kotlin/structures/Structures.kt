package structures

import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object User : Table<Nothing>("user") {
    val name = varchar("name").primaryKey()
    val password = varchar("password")
}