package plugin

interface PluginCreator<T> {

    fun create(descriptor: PluginDescriptor, name: String, pluginClassMap: Map<String, T>): Plugin<T>

}