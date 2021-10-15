import gui.Alert
import gui.Container
import gui.Data
import org.ktorm.database.Database
import org.ktorm.dsl.*
import structures.User

class CreateUser(private val db: Database) {
    private var newUsername = mutableMapOf<String, String>()
    private var newPassword = mutableMapOf<String, String>()
    private var data: Data? = null

    private suspend fun createUser(user: String) {
        kotlin.runCatching {
            if (newUsername[user] == null) {
                throw Exception("Username is empty. Please fill the field Username.")
            }
            if (newPassword[user] == null) {
                throw Exception("Password is empty. Please fill the field Password.")
            }

            val records = db.from(User).select().where {
                User.name eq newUsername[user]!!
            }.totalRecords

            if (records > 0) {
                throw Exception("User ${newUsername[user]} already exists.")
            }

            db.insert(User) {
                set(User.name, newUsername[user])
                set(User.password, newPassword[user])
            }
            Unit
        }.onFailure {
            data?.update(user, Alert("alert_create", "${it.message}"))
        }.onSuccess {
            newUsername.remove(user)
            newPassword.remove(user)
            data?.update(user, Alert("alert_create", "Successfully added ${newUsername[user]}."))
        }
    }

    fun getContainer(): Container {
        return Container.create("Create User") {
            textfield("username") {
                text = "Username"
                update = { user, text ->
                    newUsername[user] = text
                }
            }
            textfield("password") {
                text = "Password"
                update = { user, text ->
                    newPassword[user] = text
                }
            }
            button("create") {
                text = "Create"
                onClick = { user ->
                    createUser(user)
                }
            }
            data = data("alert_create_data") {}

            onInit = { user ->
                data?.update(user, Alert("alert_create", ""))
            }
        }
    }
}