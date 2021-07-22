#include <WiFiManager.h>
#include <EEPROM.h>
#include <MFRC522.h>
#include <EspMQTTClient.h>

#define RST_PIN 2
#define SS_PIN 15

#define CHIP_ID "01"


typedef struct {
  char server[40] = "";
  char user[40] = "";
  char password[40] = "";
  int port = 0;
} Config;


MFRC522 mfrc522(SS_PIN, RST_PIN);

EspMQTTClient *client = NULL;
String ssid = "";
String psk = "";
String willTopic;
Config config;

WiFiManager manager;


bool shouldSave = false;

void onSaveCallback() {
  shouldSave = true;
}

void setup() {

  Serial.begin(9600);

  char port[6] = "";
  WiFiManagerParameter mqtt_server("server", "mqtt server", config.server, 40);
  WiFiManagerParameter mqtt_password("password", "mqtt password", config.password, 40);
  WiFiManagerParameter mqtt_user("password", "mqtt user", config.password, 40);
  WiFiManagerParameter mqtt_port("port", "mqtt port", port, 6);
  manager.setSaveConfigCallback(onSaveCallback);
  manager.setConnectTimeout(60);
  manager.addParameter(&mqtt_server);
  manager.addParameter(&mqtt_port);
  manager.addParameter(&mqtt_user);
  manager.addParameter(&mqtt_password);
  auto result = manager.autoConnect("ConfigAP");

  config.port = atoi(mqtt_port.getValue());
  strcpy(config.server, mqtt_server.getValue());
  strcpy(config.user, mqtt_user.getValue());
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


  psk = WiFi.psk();
  ssid = WiFi.SSID();
  WiFi.disconnect();

  Serial.println(psk.c_str());
  Serial.println(ssid.c_str());
  Serial.println(config.server);
  Serial.println(config.port);
  Serial.println(config.user);
  Serial.println(config.password);



  client = new EspMQTTClient(
    ssid.c_str(),
    psk.c_str(),
    config.server,
    config.user,
    config.password,
    CHIP_ID,
    config.port
  );

  client->enableDebuggingMessages();
  willTopic = "doorlock/";
  willTopic += CHIP_ID;
  willTopic += "/status";
  client->enableLastWillMessage("doorlock/" CHIP_ID "/status", "{ \"active\": false }", true);


  SPI.begin();
  mfrc522.PCD_Init();
}

void onConnectionEstablished() {
  client->publish("doorlock/" CHIP_ID "/status", "{ \"active\": true }", true);
}

void loop() {

  client->loop();
  
  // Look for new cards
  if ( ! mfrc522.PICC_IsNewCardPresent()) {
    delay(50);
    return;
  }
  // Select one of the cards
  if ( ! mfrc522.PICC_ReadCardSerial()) {
    delay(50);
    return;
  }
  // Show some details of the PICC (that is: the tag/card)
  Serial.print(F("Card UID:"));
  dump_byte_array(mfrc522.uid.uidByte, mfrc522.uid.size);
  Serial.println();

}

// Helper routine to dump a byte array as hex values to Serial
void dump_byte_array(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }

}
