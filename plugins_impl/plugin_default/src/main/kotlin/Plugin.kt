import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Container
import org.ktorm.database.Database
import org.ktorm.dsl.*
import plugin.implementations.plugin.IPlugin
import structures.User

class Plugin : IPlugin {

    private var database: Database? = null
    private var username = ""
    private var password = ""
    private var oldPassword = ""
    private var newPassword1 = ""
    private var newPassword2 = ""
    private var status = false
    private var logger: ILogger? = null

    override fun init(handler: Mqtt3Client, database: Database, logger: ILogger): Boolean {
        this.database = database
        this.logger = logger
        return true
    }

    override fun close() {
    }

    override fun getContainers(): List<Container> {
        return listOf(Container.create("Add User") {
            textfield("username") {
                text = "Username: "
                update = { _, it ->
                    username = it
                }
            }
            textfield("password") {
                text = "Password: "
                update = { _, it ->
                    password = it
                }
            }
            button("submit_add") {
                text = "Add"
                onClick = {
                    try {
                        if (database != null) {
                            database!!.insert(User) {
                                set(it.name, username)
                                set(it.password, password)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        println("Failed")
                    }
                }
            }
        }, Container.create("Change Password") {
            textfield("old_pw") {
                text = "Old Password"
                update = { _, it ->
                    oldPassword = it
                }
            }
            textfield("new_pw") {
                text = "New Password"
                update = { _, it ->
                    newPassword1 = it
                }
            }
            textfield("new_pw2") {
                text = "Repeat Password"
                update = { _, it ->
                    newPassword2 = it
                }
            }
            button("submit_change") {
                text = "Change"
                onClick = {
                    try {
                        logger?.debug { "Database is null?: ${database == null}" }
                        if (database != null) {
                            if (newPassword1.trim() == newPassword2.trim()) {
                                val result = database!!.from(User).select().where {
                                    User.name eq it
                                }
                                username = ""
                                password = ""
                                for (line in result) {
                                    username = line[User.name].toString().trim()
                                    password = line[User.password].toString().trim()
                                    break
                                }
                                logger?.debug { "Username: $username" }
                                logger?.debug { "OldPassword: $oldPassword" }
                                logger?.debug { "Password: $password" }
                                logger?.debug { "New1: $newPassword1" }
                                logger?.debug { "New2: $newPassword2" }
                                if (username != "" && password == oldPassword) {
                                    database!!.update(User) {
                                        set(User.password, newPassword1.trim())
                                        where {
                                            it.name eq username
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        })
    }


}