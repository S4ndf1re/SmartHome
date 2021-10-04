package plugin

import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import org.ktorm.database.Database


abstract class Plugin<T> {

    abstract val name: String
    abstract val descriptor: PluginDescriptor
    abstract val pluginClassMap: Map<String, T>

    abstract fun start(client: Mqtt3Client, database: Database, logger: ILogger)

    abstract fun stop()

}
