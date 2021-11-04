#!bin/bash
scp backend/build/libs/backend-1.0-SNAPSHOT.jar pi@192.168.100.54:~/backend.jar
scp controller_impl/default_controller/build/libs/default_controller-1.0-SNAPSHOT.jar pi@192.168.100.54:~/controller/default
scp plugins_impl/plugin_default/build/libs/plugin_default-1.0-SNAPSHOT.jar pi@192.168.100.54:~/plugins/default/
scp plugins_impl/plugin_doorlock_esp8266/build/libs/plugin_doorlock_esp8266-1.0-SNAPSHOT.jar pi@192.168.100.54:~/plugins/doorlock

scp controller_impl/default_controller/frontend/build.zip pi@192.168.100.54:~/build.zip
