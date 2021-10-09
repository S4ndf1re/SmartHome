package plugin

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import java.io.FileReader
import java.io.FileWriter

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
        fun load(path: Path): Result<PluginDescriptor> {
            return try {
                FileReader(path).use { file ->
                    val xml = XML {
                        indentString = "    "
                        xmlDeclMode = XmlDeclMode.Minimal
                    }
                    Result.success(xml.decodeFromString(file.readText()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * save will save a PluginDescriptor to a file
     * @param path The path from where to find the plugin.xml
     */
    fun save(path: Path) {
        val xml = XML {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
        }
        FileWriter(path).use { it.write(xml.encodeToString(this)) }
    }
}