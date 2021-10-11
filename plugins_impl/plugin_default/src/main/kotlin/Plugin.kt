import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Container
import org.ktorm.database.Database
import plugin.implementations.plugin.IPlugin

class Plugin : IPlugin {

    private var createUser: CreateUser? = null
    private var changePassword: ChangePassword? = null

    override fun init(handler: Mqtt3Client, database: Database, logger: ILogger): Boolean {
        this.createUser = CreateUser(database)
        this.changePassword = ChangePassword(database)
        return true
    }

    override fun close() {
    }

    override fun getContainers(): List<Container> {
        val mutList = mutableListOf<Container>()
        this.createUser?.getContainer()?.let { mutList.add(it) }
        this.changePassword?.getContainer()?.let { mutList.add(it) }
        return mutList
    }


}