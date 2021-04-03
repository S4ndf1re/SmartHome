package plugin

import java.io.File
import java.net.URLClassLoader


/**
 * Extension Loader is a helper class to retrieve Plugins from a directory.
 */
class ExtensionLoader<T> {

    /**
     * loadFromDir takes a directory and generates type T
     * This method will take all instances of [classnames] and convert them
     * into usable objects. All objects are a parent of [parent]
     * @param classnames The class name to search for in jar file
     * @param parent The parent class to inherit from
     * @return A mutable list of type T objects
     */
    fun loadFromDir(dir: Path, classnames: ArrayList<String>, parent: Class<T>): MutableMap<String, T> {
        val map = mutableMapOf<String, T>()
        val pluginsFile = File(dir)
        try {
            val classLoader =
                URLClassLoader.newInstance(arrayOf(pluginsFile.toURI().toURL()), this::class.java.classLoader)
            for (classname in classnames) {
                val clazz = Class.forName(classname, true, classLoader)
                val extendedClass: Class<out T> = clazz.asSubclass(parent)

                val constructor = extendedClass.getConstructor()
                map[pluginsFile.name] = constructor.newInstance()
            }

        } catch (e: Exception) {

        }

        return map
    }

}