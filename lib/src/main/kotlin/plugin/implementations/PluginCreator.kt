package plugin.implementations

import plugin.IPlugin
import plugin.Plugin
import plugin.PluginCreator
import plugin.PluginDescriptor

class PluginCreator : PluginCreator<IPlugin> {
    override fun create(
        descriptor: PluginDescriptor,
        name: String,
        pluginClassMap: Map<String, IPlugin>,
    ): Plugin<IPlugin> {
        return PluginImpl(name = name, descriptor = descriptor, pluginClassMap = pluginClassMap)
    }

}