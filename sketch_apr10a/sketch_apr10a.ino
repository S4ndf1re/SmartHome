#include <WiFiManager.h>
#include <EEPROM.h>
#include <MFRC522.h>
#include <EspMQTTClient.h>
#include <Base64.h>

#define RST_PIN 2
#define SS_PIN 15

#define MAX_BYTES 48
#define DEFAULT_BLOCK 4 // This will start at Sector 0, Block 0
#define TRAILING_SECTOR 7 // Authentication sector.

#define CHIP_ID "01"


typedef struct {
  char server[40] = "";
  char user[40] = "";
  char password[40] = "";
  int port = 0;
} Config;


MFRC522 mfrc522(SS_PIN, RST_PIN);
MFRC522::MIFARE_Key key;

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

// Init default key. This may change during development
void initRfidKey() {
  for (byte i = 0; i < 6; i++) {
    key.keyByte[i] = 0xFF;
  }
}

void setup() {

  Serial.begin(9600);

  // Try connect to wifi and or mqtt.
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


  // Save mqtt data
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

  initRfidKey();
}

bool compareByteArray(byte* a, int sizeA, byte* b, int sizeB) {
  if (sizeA != sizeB) {
    return false;
  }
  for (int i = 0; i < sizeA; i++) {
    if (a[i] != b[i]) {
      return false;
    }
  }
  return true;
}

void safeWrite(byte* data, int size) {
  if (size > MAX_BYTES) {
    client->publish("doorlock/" CHIP_ID "/error", "{ \"error\": \"To many bytes. Max size is 48 bytes.\"");
    return;
  }

  MFRC522::StatusCode status;


  status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, TRAILING_SECTOR, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
    return;
  }

  byte buffer[16] = {0};
  int offset = 0;
  int blockAddr = DEFAULT_BLOCK;
  for (int i = 0; i < 3; i++, blockAddr++) {
    int remaining = min(16, size - offset);
    for (int j = 0; j < remaining; j++) {
      buffer[j] = data[j + offset];
    }
    offset += remaining;
    if (remaining == 0) {
      break;
    }

    bool success = false;
    for (int tryIdx = 0; tryIdx < 3; tryIdx++) {
      status = (MFRC522::StatusCode) mfrc522.MIFARE_Write(blockAddr, buffer, 16);
      byte newBuffer[16];
      byte bufferSize = 16;
      status = (MFRC522::StatusCode) mfrc522.MIFARE_Read(blockAddr, newBuffer, &bufferSize);
      if (compareByteArray(buffer, 16, newBuffer, 16)) {
        success = true;
        break;
      }
    }

    if (!success) {
      client->publish("doorlock/" CHIP_ID "/error", "{ \"error\": \"After 3 tries, data could not be written onto chip.\"");
      break;
    }

  }

  // Halt PICC
  mfrc522.PICC_HaltA();
  // Stop encryption on PCD
  mfrc522.PCD_StopCrypto1();
}

void onDoorlockWrite(const String &msg) {
  // TODO Base64 Decode
  safeWrite((byte*)msg.c_str(), msg.length());
}


void onConnectionEstablished() {
  client->publish("doorlock/" CHIP_ID "/status", "{ \"active\": true }", true);
  client->subscribe("doorlock/" CHIP_ID "/write", onDoorlockWrite);
}


void rfidRead(byte* data, int size) {
  if (size > MAX_BYTES) {
    client->publish("doorlock/" CHIP_ID "/error", "{ \"error\": \"To many bytes. Max size is 48 bytes.\"");
    return;
  }

  MFRC522::StatusCode status;


  status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, TRAILING_SECTOR, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
    return;
  }

  byte buffer[16] = {0};
  byte bufferSize = 16;
  int offset = 0;
  int blockAddr = DEFAULT_BLOCK;
  for (int i = 0; i < 3; i++, blockAddr++) {
    int remaining = min(16, size - offset);

    offset += remaining;
    if (remaining == 0) {
      break;
    }
    status = (MFRC522::StatusCode) mfrc522.MIFARE_Read(blockAddr, buffer, &bufferSize);

    for (int j = 0; j < remaining; j++) {
      data[j + offset] = buffer[j];
    }
  }
}

void rfidLoop() {
  
  if ( ! mfrc522.PICC_IsNewCardPresent())
    return;

  // Select one of the cards
  if ( ! mfrc522.PICC_ReadCardSerial())
    return;

  byte buffer[MAX_BYTES + 1];
  rfidRead(buffer, MAX_BYTES);
  buffer[MAX_BYTES] = '\0';
  // TODO Base64 Encode
  String data = "{ \"uid\": \"";
  data += mfrc522.uid;
  data += "\", \"data\": \"";
  data += buffer;
  data += "\" }";
  Serial.println(data);
  // client->publish("doorlock/" CHIP_ID "/read", data);
  
}

void loop() {

  client->loop();
  rfidLoop();

}

// Helper routine to dump a byte array as hex values to Serial
String dump_byte_array(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }

}
