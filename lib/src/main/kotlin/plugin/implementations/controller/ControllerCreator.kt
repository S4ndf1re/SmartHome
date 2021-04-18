package plugin.implementations.controller

import plugin.Plugin
import plugin.PluginCreator
import plugin.PluginDescriptor
import plugin.implementations.plugin.IPlugin

class ControllerCreator(val plugins: Map<String, Plugin<IPlugin>>) : PluginCreator<IController> {
    override fun create(
        descriptor: PluginDescriptor,
        name: String,
        pluginClassMap: Map<String, IController>,
    ): Plugin<IController> {
        return ControllerImpl(name = name, descriptor = descriptor, pluginClassMap = pluginClassMap, plugins = plugins)
    }

}