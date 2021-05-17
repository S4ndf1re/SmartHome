# SmartHome

TODO

# Dependencies

- [HiveMQ MQTT Broker community edition](https://github.com/hivemq/hivemq-community-edition)
- [Dokka Documentation Tool for Kotlin](https://github.com/Kotlin/dokka)

# Documenting the code

The code can be documented as following:

```
.\gradlew :lib:dokkaHtml
.\gradlew :backend:dokkaHtml
```

# "Installing"

To use this Smarthome tool, you have to do the following:

1. Make sure to have a reachable MySql database (currently, only MySql is supported)
2. Create on database using the sql_script.sql file.
3. Build using .\gradlew :backend:build
4. Put the generated jar from build/libs into ./
5. Create all plugins in the ./plugins folder.
6. Create all controller in the ./controller folder.

# Plugins

There is only one test plugin to check whether your build works. It can be found in plugin_test

# Controller

There is one default http controller. The controller resembles just an API. In order to use it, you should consider
using the Smarthome_frontend repository.