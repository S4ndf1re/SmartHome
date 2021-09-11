/*
serialRelayTest.ino
*/

#include <ESP8266WiFi.h>
#include <RNG.h>
#include <SHA256.h>

WiFiServer server(80);
char secret[33] = "VkYp3s6v9y$B&E)H+MbQeThWmZq4t7w!";
byte relON[] = {0xA0, 0x01, 0x01, 0xA2};
// Hex command to send to serial for open relay
byte relOFF[] = {0xA0, 0x01, 0x00, 0xA1};
// Hex command to send to serial for close relay
char alphabet[53] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
void setup(void) {
  Serial.begin(9600);
  // Serial.println("Starting");
  // make sure the relay is off before moving forward
  Serial.write(relOFF, sizeof(relOFF));
  delay(10);
  Serial.write(relOFF, sizeof(relOFF));

  connect();
  // Serial.print("IP: ");
  // Serial.println(WiFi.localIP());

  server.begin();
}

void connect() {
  if (WiFi.status() == WL_CONNECTED) {
    return;
  }
  WiFi.mode(WIFI_STA);
  WiFi.begin("HeinzBox", "ZauberBude!!XX");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void genNonce(char *seed, uint8_t *buffer, int len) {
  RNG.begin(seed);
  RNG.rand(buffer, len);

  for (int i = 0; i < len; i++) {
    buffer[i] = alphabet[buffer[i] % (sizeof(alphabet) - 1)];
  }
}

void readSeed(const char *src, int from, int to, char *buffer, int len) {
  int indexBuffer = 0;
  for (int i = from; i <= to && indexBuffer < len; i++, indexBuffer++) {
    buffer[indexBuffer] = src[i];
  }
}

void encrypt(char *nonce, int nonceLen, char *plaintext, char *ciphertext,
             int len) {
  if (len < 32) {
    return;
  }
  char key[256];
  SHA256 sha;
  sha.update(nonce, nonceLen);
  sha.update(secret, sizeof(secret) - 1);
  sha.finalize(ciphertext, len - 1);

  ciphertext[len - 1] = '\0';

  for (int i = 0; i < len - 1; i++) {
    ciphertext[i] = ciphertext[i] % 26 + 'a';
  }
}

int equal(char *buff1, int len1, char *buff2, int len2) {
  if (len1 != len2) {
    return 0;
  }

  for (int i = 0; i < len1; i++) {
    if (buff1[i] != buff2[i]) {
      return i;
    }
  }
  return -1;
}

void loop(void) {
  connect();
  char encrypted[33] = "";
  delay(100);
  WiFiClient client = server.available();
  if (client) {
    // Note that there can only be one controlling client at a time
    while (client.connected()) {
      if (client.available()) {
        String line = client.readStringUntil('\r');

        if (line.length() == 1 && line[0] == '\n') {
          break;
        }

        line.trim();
        if (line.startsWith("NONCE ") && line.length() == 16) {
          char seed[11];
          readSeed(line.c_str(), 6, 15, seed, sizeof(seed));
          seed[10] = '\0';
          // Nonce size of 12 (as recommended for gcm auth encryption)
          // But buffer size of 13 because of trailing \0
          char buffer[13];
          genNonce(seed, (uint8_t *)buffer, sizeof(buffer));
          buffer[12] = '\0';
          String answer = String(buffer);
          answer = "NONCEBACK " + answer + "\r";
          client.println(answer);
          encrypt(buffer, sizeof(buffer) - 1, secret, encrypted,
                  sizeof(secret));
        } else if (line.startsWith(String("ANS ") + String(encrypted))) {
          client.println("OK\r");
          Serial.write(relON, sizeof(relON));
          delay(3000);
          Serial.write(relOFF, sizeof(relOFF));
        } else {
          client.println("ERR\r");
        }
      }
    }
  }
}
