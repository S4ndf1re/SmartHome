import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Child
import gui.Gui
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import plugin.Plugin
import plugin.implementations.controller.IController
import plugin.implementations.plugin.IPlugin
import structures.User
import java.util.concurrent.TimeUnit

@Serializable
data class TextInputData(val text: String)

class Plugin : IController {
    private var pluginList: Map<String, Plugin<IPlugin>> = mapOf()
    private var gui: Gui = Gui.create { }
    private var server: NettyApplicationEngine? = null

    private fun configureOnOffState(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.OnOffState) {
            child.onOnStateRequest = "$key/$name/${child.name}/on".lowercase()
            child.onOffStateRequest = "$key/$name/${child.name}/off".lowercase()
            child.onGetStateRequest = "$key/$name/${child.name}/get".lowercase()
            routing.get(child.onOnStateRequest) {
                child.onOnState(call.principal<UserIdPrincipal>()?.name!!)
                call.respondText(
                    "{ \"status\": ${child.getCurrent(call.principal<UserIdPrincipal>()?.name!!)} }",
                    status = HttpStatusCode.OK
                )
            }
            routing.get(child.onOffStateRequest) {
                child.onOffState(call.principal<UserIdPrincipal>()?.name!!)
                call.respondText(
                    "{ \"status\": ${child.getCurrent(call.principal<UserIdPrincipal>()?.name!!)} }",
                    status = HttpStatusCode.OK
                )
            }
            routing.get(child.onGetStateRequest) {
                call.respondText(
                    "{ \"status\": ${child.getCurrent(call.principal<UserIdPrincipal>()?.name!!)} }",
                    status = HttpStatusCode.OK
                )
            }
        }
    }

    private fun configureClickable(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.Clickable) {
            child.onClickRequest = "$key/$name/${child.name}".lowercase()
            routing.get(child.onClickRequest) {
                child.onClick(call.principal<UserIdPrincipal>()?.name!!)
                call.respondText("", status = HttpStatusCode.OK)
            }
        }
    }

    private fun configureTextInput(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.TextInput) {
            child.updateRequest = "$key/$name/${child.name}".lowercase()
            routing.post(child.updateRequest) {
                val data = call.receive<TextInputData>()
                child.update(call.principal<UserIdPrincipal>()?.name!!, data.text)
                call.respondText("", status = HttpStatusCode.OK)
            }

        }
    }

    private fun configureData(routing: Route, child: Child, key: String, name: String) {
        if (child is gui.Data) {
            child.updateRequest = "$key/$name/${child.name}/request".lowercase()
            child.updateSocket = "$key/$name/${child.name}/socket".lowercase()
            routing.webSocket(child.updateSocket) {
                val updateFunction: suspend (Child) -> Unit = { element ->
                    val json = Gui.getJsonDefault()
                    val jsonString = json.encodeToString(element)
                    outgoing.send(Frame.Text(jsonString))
                }
                child.registerUpdateFunction(updateFunction)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Close -> {
                            child.unregisterUpdateFunction(updateFunction)
                        }
                        else -> {
                            // Do nothing. We are not expecting to receive any data
                        }
                    }
                }
            }
            routing.get(child.updateRequest) {
                val json = Gui.getJsonDefault()
                if (child.getState() == null) {
                    call.respond(HttpStatusCode.NoContent, "")
                } else {
                    val jsonString = json.encodeToString(child.getState()!!)
                    call.respondText { jsonString }
                }
            }
        }
    }

    override fun init(handler: Mqtt3Client, database: Database, pluginList: Map<String, Plugin<IPlugin>>): Boolean {
        this.pluginList = pluginList

        this.server = embeddedServer(Netty, port = 1337) {
            install(CORS) {
                header(HttpHeaders.AccessControlAllowHeaders)
                header(HttpHeaders.AccessControlAllowOrigin)
                anyHost()
                allowNonSimpleContentTypes = true
                allowSameOrigin = true
                allowCredentials = true
            }
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                basic("login") {
                    realm = "Access to API"
                    validate { credentials ->
                        val results = database.from(User).select().where {
                            User.name eq credentials.name
                        }
                        for (row in results) {
                            if (row[User.name].equals(credentials.name) && row[User.password].equals(credentials.password)) {
                                return@validate row[User.name]?.let { UserIdPrincipal(it) }
                            }
                        }
                        null
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
                                    configureData(this, child, key, name)
                                }
                            }
                        }
                    }

                    get("gui") {
                        val js = Gui.getJsonDefault()
                        call.respondText(js.encodeToString(gui), status = HttpStatusCode.OK)
                    }
                }
            }
        }.start(wait = false)


        return true
    }

    override fun close() {
        this.server?.stop(gracePeriod = 10, timeout = 10, timeUnit = TimeUnit.SECONDS)
    }
}
