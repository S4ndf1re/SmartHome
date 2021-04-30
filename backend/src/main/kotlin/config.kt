import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import java.io.File

const val configPath = "config.xml"

@Serializable
data class Config(
    @XmlElement(true)
    val identifier: String,
    @XmlElement(true)
    val hostname: String,
    @XmlElement(true)
    val port: Int,
    @XmlElement(true)
    val username: String,
    @XmlElement(true)
    val password: String,
) {

    companion object Factory {

        /**
         * loadConfig will load either the config defined in [configPath] or the default config
         * @return Configuration file
         */
        fun loadConfig(): Config {
            return try {
                val file = File(configPath)
                val text = file.readText()
                configFromXml(text)
            } catch (e: Exception) {
                default()
            }
        }


        /**
         * defaultConfig simply generates a filled config with default values
         */
        private fun default(): Config {
            return Config("mqtt_backend", "localhost", 1883, "", "")
        }

        /**
         * configFromXml will generate a config file from xmlString
         */
        private fun configFromXml(xmlPayload: String): Config {
            val xml = XML {
                indentString = "    "
                xmlDeclMode = XmlDeclMode.Minimal
            }
            return xml.decodeFromString(xmlPayload)
        }
    }

    /**
     * asXmlString will write the file to xml as String
     */
    private fun asXmlString(): String {
        val xml = XML {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
        }
        return xml.encodeToString(this)
    }

    /**
     * save will safe the config in [configPath]
     */
    fun save() {
        val file = File(configPath)
        file.writeText(this.asXmlString())
    }

}

