package plugin


class Plugin(
    val name: String,
    val descriptor: PluginDescriptor,
    val pluginClassMap: Map<String, IPlugin>,
) {


}
