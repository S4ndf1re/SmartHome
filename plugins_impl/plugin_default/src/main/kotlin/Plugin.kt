import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Container
import org.ktorm.database.Database
import org.ktorm.dsl.*
import plugin.implementations.plugin.IPlugin
import structures.User
import java.util.*

class Plugin : IPlugin {

    private var database: Optional<Database> = Optional.empty()
    private var username = ""
    private var password = ""
    private var oldPassword = ""
    private var newPassword1 = ""
    private var newPassword2 = ""
    private var logger: Optional<ILogger> = Optional.empty()

    override fun init(handler: Mqtt3Client, database: Database, logger: ILogger): Boolean {
        this.database = Optional.of(database)
        this.logger = Optional.of(logger)
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
                    kotlin.runCatching {
                        database.ifPresent { db ->
                            db.insert(User) {
                                set(it.name, username)
                                set(it.password, password)
                            }
                        }
                    }.onFailure { exception ->
                        logger.ifPresent { log ->
                            log.error { exception.toString() }
                        }
                    }
                }
            }
        }, Container.create("Change Password")
        {
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
                    kotlin.runCatching {
                        database.ifPresent { db ->
                            if (newPassword1.trim() == newPassword2.trim()) {
                                val result = db.from(User).select().where {
                                    User.name eq it
                                }
                                username = ""
                                password = ""
                                for (line in result) {
                                    username = line[User.name].toString().trim()
                                    password = line[User.password].toString().trim()
                                    break
                                }
                                if (username != "" && password == oldPassword) {
                                    db.update(User) {
                                        set(User.password, newPassword1.trim())
                                        where {
                                            it.name eq username
                                        }
                                    }
                                }
                            }
                        }
                    }.onFailure { exception ->
                        logger.ifPresent { log ->
                            log.error { exception.toString() }
                        }
                    }
                }
            }
        })
    }


}