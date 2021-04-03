package plugin

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import util.Author
import util.Path
import util.Version
import java.io.File

/**
 * PluginDescriptor serves as the registration for a new Plugin
 * In order for this config to be found, a file named plugin.xml must be
 * in the same directory as the single plugin.jar itself
 * plugin.jar should contain all dependencies it needs.
 */
@Serializable
data class PluginDescriptor(
    @XmlElement(true)
    val author: Author,
    @XmlElement(true)
    val version: Version,
    @XmlElement(true)
    val jarName: Path,
    @XmlElement(true)
    val pluginClass: ArrayList<String>,
) {
    companion object Factory {
        /**
         * default generates the basic, and useless PluginDescriptor
         * @return A new PluginDescriptor
         */
        fun default(): PluginDescriptor {
            return PluginDescriptor(
                author = "None",
                version = "1.0.0",
                jarName = "Plugin.jar",
                pluginClass = arrayListOf("SampleClass1", "SampleClass2")
            )
        }

        /**
         * load will load any plugin.xml when in correct format
         * @param path The path from where to find the plugin.xml
         * @return A new, possibly default PluginDescriptor
         */
        fun load(path: Path): PluginDescriptor {
            return try {
                val file = File(path)
                val xml = XML {
                    indentString = "    "
                    xmlDeclMode = XmlDeclMode.Minimal
                }
                xml.decodeFromString(file.readText())
            } catch (e: Exception) {
                default()
            }
        }
    }

    /**
     * save will save a PluginDescriptor to a file
     * @param path The path from where to find the plugin.xml
     */
    fun save(path: Path) {
        val file = File(path)

        val xml = XML {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
        }

        file.writeText(xml.encodeToString(this))
    }
}