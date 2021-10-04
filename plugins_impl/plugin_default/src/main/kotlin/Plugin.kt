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

    override fun init(handler: Mqtt3Client, database: Database, logger: ILogger): Boolean {
        this.database = database
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
            checkbox("cb1") {
                text = "check me"
                onOnState = {
                    println("Me on ")
                    status = true
                }
                onOffState = { user ->
                    println("Me off ")
                    println(user)
                    status = false
                }
                getCurrent = {
                    status
                }
            }
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
                        if (database != null) {
                            if (newPassword1 == newPassword2) {
                                val result = database!!.from(User).select().where {
                                    User.name eq it
                                }
                                username = ""
                                for (line in result) {
                                    username = line[User.name].toString()
                                    break
                                }
                                if (username != "") {
                                    database!!.update(User) {
                                        set(User.password, newPassword1)
                                        where {
                                            it.name eq username
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        })
    }


}