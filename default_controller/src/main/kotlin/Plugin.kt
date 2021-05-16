import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Child
import gui.Gui
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import plugin.Plugin
import plugin.implementations.controller.IController
import plugin.implementations.plugin.IPlugin

@Serializable
data class TextInputData(val text: String)

class Plugin : IController {
    private var pluginList: Map<String, Plugin<IPlugin>> = mapOf();
    private var gui: gui.Gui = Gui.create { }

    private fun configureOnOffState(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.OnOffState) {
            child.onOnStateRequest = "$key/$name/${child.name}/on".toLowerCase()
            child.onOffStateRequest = "$key/$name/${child.name}/off".toLowerCase()
            child.onGetStateRequest = "$key/$name/${child.name}/get".toLowerCase()
            routing.get(child.onOnStateRequest) {
                child.onOnState()
                call.respondText("{ \"status\": ${child.getCurrent()} }",
                    status = HttpStatusCode.OK)
            }
            routing.get(child.onOffStateRequest) {
                child.onOffState()
                call.respondText("{ \"status\": ${child.getCurrent()} }",
                    status = HttpStatusCode.OK)
            }
            routing.get(child.onGetStateRequest) {
                call.respondText("{ \"status\": ${child.getCurrent()} }",
                    status = HttpStatusCode.OK)
            }
        }
    }

    private fun configureClickable(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.Clickable) {
            child.onClickRequest = "$key/$name/${child.name}".toLowerCase()
            routing.get(child.onClickRequest) {
                child.onClick()
                call.respondText("", status = HttpStatusCode.OK)
            }
        }
    }

    private fun configureTextInput(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.TextInput) {
            child.updateRequest = "$key/$name/${child.name}".toLowerCase()
            routing.post(child.updateRequest) {
                val data = call.receive<TextInputData>()
                child.update(data.text)
                call.respondText("", status = HttpStatusCode.OK)
            }

        }
    }

    override fun init(handler: Mqtt3Client, pluginList: Map<String, Plugin<IPlugin>>): Boolean {
        this.pluginList = pluginList

        embeddedServer(Netty, port = 1337) {
            install(CORS) {
                header(HttpHeaders.AccessControlAllowHeaders)
                header(HttpHeaders.AccessControlAllowOrigin)
                anyHost()
                allowNonSimpleContentTypes = true
                allowSameOrigin = true
                allowCredentials = true
            }
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                basic("login") {
                    realm = "Access to API"
                    validate { credentials ->
                        if (credentials.name == "jetbrains" && credentials.password == "foobar") {
                            UserIdPrincipal(credentials.name)
                        } else {
                            null
                        }
                    }
                }
            }
            routing {
                authenticate("login") {
                    pluginList.forEach { (key, value) ->
                        value.pluginClassMap.forEach { (name, plugin) ->
                            val containers = plugin.getContainers()
                            containers.forEach {
                                gui.add(it)
                                it.list.forEach { child ->
                                    configureClickable(this, child, key, name)
                                    configureOnOffState(this, child, key, name)
                                    configureTextInput(this, child, key, name)
                                }
                            }
                        }
                    }

                    get("gui") {
                        val js = gui.getJsonDefault()
                        call.respondText(js.encodeToString(gui), status = HttpStatusCode.OK)

                    }
                }
            }
        }.start(wait = true)


        return true
    }

    override fun close(handler: Mqtt3Client) {
    }
}
