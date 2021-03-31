package util

import java.io.File
import java.net.URLClassLoader

typealias Path = String

/**
 * Extension Loader is a helper class to retrieve Plugins from a directory.
 */
class ExtensionLoader<T> {

    /**
     * loadFromDir takes a directory and generates type T
     * This method will take all instances of [classname] and convert them
     * into usable objects. All objects are a parent of [parent]
     * @param classname The class name to search for in jar file
     * @param parent The parent class to inherit from
     * @return A mutable list of type T objects
     */
    fun loadFromDir(dir: Path, classname: String, parent: Class<T>): MutableMap<String, T> {
        val map = mutableMapOf<String, T>()
        val pluginsDir = File(dir)
        val fileList = pluginsDir.listFiles() ?: return map
        for (f in fileList) {
            try {
                val classLoader =
                    URLClassLoader.newInstance(arrayOf(f.toURI().toURL()), this::class.java.classLoader)
                val clazz = Class.forName(classname, true, classLoader)
                val extendedClass: Class<out T> = clazz.asSubclass(parent)

                val constructor = extendedClass.getConstructor()
                map[f.name] = constructor.newInstance()

            } catch (e: Exception) {

            }
        }


        return map
    }

}