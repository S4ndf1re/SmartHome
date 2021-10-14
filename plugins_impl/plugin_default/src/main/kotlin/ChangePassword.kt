import gui.Alert
import gui.Container
import gui.Data
import org.ktorm.database.Database
import org.ktorm.dsl.*
import structures.User

class ChangePassword(private val db: Database) {
    private var oldPassword = mutableMapOf<String, String>()
    private var newPassword1 = mutableMapOf<String, String>()
    private var newPassword2 = mutableMapOf<String, String>()
    private var data: Data? = null


    private suspend fun changePW(user: String) {
        kotlin.runCatching {
            if (oldPassword[user] == null) {
                throw Exception("Old password is not supplied.")
            } else if (newPassword1[user] == null) {
                throw Exception("New Password is not supplied.")
            } else if (newPassword2[user] == null) {
                throw Exception("Repeat password is not supplied.")
            }
            val pw1 = newPassword1[user]!!
            val pw2 = newPassword2[user]!!
            val pwOld = oldPassword[user]!!

            if (pw1.trim() == pw2.trim()) {
                val result = db.from(User).select().where {
                    User.name eq user
                }.limit(1)
                var username = ""
                var password = ""
                for (line in result) {
                    username = line[User.name].toString().trim()
                    password = line[User.password].toString().trim()
                    break
                }
                if (username != "" && password == pwOld) {
                    db.update(User) {
                        set(User.password, pw1.trim())
                        where {
                            it.name eq username
                        }
                    }
                } else {
                    throw Exception("Username or Password is wrong.")
                }
            }
        }.onFailure {
            data?.update(user, Alert("change_pw_alert", "${it.message}"))
        }.onSuccess {
            data?.update(user, Alert("change_pw_alert", "Changed password successfully."))
        }
    }

    fun getContainer(): Container {
        return Container.create("Change Password") {
            textfield("old_pw") {
                text = "Old Password"
                update = { user, it ->
                    oldPassword[user] = it
                }
            }
            textfield("new_pw") {
                text = "New Password"
                update = { user, it ->
                    newPassword1[user] = it
                }
            }
            textfield("new_pw2") {
                text = "Repeat Password"
                update = { user, it ->
                    newPassword2[user] = it
                }
            }
            button("submit_change") {
                text = "Change"
                onClick = { user ->
                    kotlin.runCatching {
                        changePW(user)
                    }.onFailure {
                        data?.update(user, Alert("change_pw_alert", "${it.message}"))
                    }
                }
            }
            data = data("change_pw_data") {}

            onInit = { user ->
                data?.update(user, Alert("change_pw_alert", ""))
            }
        }
    }
}