#include <WiFiManager.h>
#include <EEPROM.h>

WiFiManager manager;

typedef struct {
  char server[40] = "";
  char user[40] = "";
  char password[40] = "";
  int port = 0;
} Config;

bool shouldSave = false;

void onSaveCallback() {
  shouldSave = true;
}

void setup() {

  Serial.begin(9600);
  
  Config config;
  char port[6] = "";
  WiFiManagerParameter mqtt_server("server", "mqtt server", config.server, 40);
  WiFiManagerParameter mqtt_password("password", "mqtt password", config.password, 40);
  WiFiManagerParameter mqtt_user("password", "mqtt user", config.password, 40);
  WiFiManagerParameter mqtt_port("port", "mqtt port", port, 6);
  manager.setSaveConfigCallback(onSaveCallback);
  manager.setConnectTimeout(20);
  manager.addParameter(&mqtt_server);
  manager.addParameter(&mqtt_port);
  manager.addParameter(&mqtt_user);
  manager.addParameter(&mqtt_password);
  auto result = manager.autoConnect("ConfigAP");

  config.port = atoi(mqtt_port.getValue());
  strcpy(config.server, mqtt_server.getValue());
  strcpy(config.user,mqtt_user.getValue());
  strcpy(config.password, mqtt_password.getValue());



  EEPROM.begin(sizeof(config));
  if (shouldSave && result) {
     Serial.println("Should save");
     EEPROM.put(0, config);
     EEPROM.commit();    
  } else {
    EEPROM.get(0, config);  
  }
  EEPROM.end();


  Serial.println("Connected...");
  Serial.println(config.server);
  Serial.println(config.port);
  Serial.println(config.user);
  Serial.println(config.password);
}


void loop() {
  
}
