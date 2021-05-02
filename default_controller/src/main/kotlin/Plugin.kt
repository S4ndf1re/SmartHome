import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Gui
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.encodeToString
import plugin.Plugin
import plugin.implementations.controller.IController
import plugin.implementations.plugin.IPlugin

class Plugin : IController {
    private var pluginList: Map<String, Plugin<IPlugin>> = mapOf();
    private var gui: gui.Gui = Gui.create { }

    override fun init(handler: Mqtt3Client, pluginList: Map<String, Plugin<IPlugin>>): Boolean {
        this.pluginList = pluginList

        embeddedServer(Netty, port = 1337) {
            install(CORS) {
                header(HttpHeaders.AccessControlAllowHeaders)
                header(HttpHeaders.AccessControlAllowOrigin)
                anyHost()
                allowSameOrigin = true
            }
            routing {
                pluginList.forEach { (key, value) ->
                    value.pluginClassMap.forEach { (name, plugin) ->
                        var container = plugin.getContainer(name)
                        gui.add(container)
                        container.list.forEach { child ->
                            if (child is gui.Clickable) {
                                child.onClickRequest = "$key/$name/${child.name}".toLowerCase()
                                get("$key/$name/${child.name}".toLowerCase()) {
                                    child.onClick()
                                    call.respondText("", status = HttpStatusCode.OK)
                                }
                            }
                        }
                    }
                }

                get("gui") {
                    val js = gui.getJsonDefault()
                    call.respondText(js.encodeToString(gui), status = HttpStatusCode.OK)

                }
            }
        }.start(wait = true)


        return true
    }

    override fun close(handler: Mqtt3Client) {
    }
}
