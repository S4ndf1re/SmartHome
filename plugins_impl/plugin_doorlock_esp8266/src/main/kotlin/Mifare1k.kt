import org.ktorm.schema.Table
import org.ktorm.schema.bytes
import org.ktorm.schema.varchar

object Mifare1k : Table<Nothing>("mifare1k") {
    var uid = bytes("uid").primaryKey()
    var data = bytes("data")
    var username = varchar("username")
}