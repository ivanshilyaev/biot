#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
#include "standard77.hpp"
#include "base64.hpp"
#include "secrets.h"

const char* PARAM_INPUT_1 = "param1";
const char* PARAM_INPUT_2 = "param2";
const char* ERROR_MESSAGE = "error";
const char* ENCODED_ON_COMMAND = "6f6e6e";
const char* ENCODED_OFF_COMMAND = "6f6666";

ESP8266WebServer server(80);

int LED = D1;

string ENCRYPTION_KEY;

int messageCount;

void healthCheck() {
  server.send(200, "text/plain", "Ok");
}

string decrypt(string key) {
  if (server.hasArg(PARAM_INPUT_1) && server.hasArg(PARAM_INPUT_2)) {
    string encodedMessage = server.arg(PARAM_INPUT_1).c_str();
    string encodedMac = server.arg(PARAM_INPUT_2).c_str();

    uint8_t hexMessageArray[BASE64::decodeLength(encodedMessage.c_str())];
    BASE64::decode(encodedMessage.c_str(), hexMessageArray);
    string hexMessage = reinterpret_cast<char *>(hexMessageArray);

    Serial.println("encrypted message:");
    Serial.println(hexMessage.c_str());

    uint8_t hexMacArray[32];
    BASE64::decode(encodedMac.c_str(), hexMacArray);
    string hexMac = reinterpret_cast<char *>(hexMacArray); 

    Serial.println("mac:");
    Serial.println(hexMac.c_str());

    size_t l = 256;
    size_t d = 1;
    
    string countString = std::to_string(++messageCount);
    uint8_t A[countString.length()];
    Serial.println("count:");
    Serial.println(messageCount);
    char aChar[countString.length()];
    strcpy(aChar, countString.c_str());
    for (int i = 0; i < countString.length(); ++i) {
      A[i] = (uint8_t)aChar[i];
    }
    uint8_t K[32];
    reverseAndDecode(K, key);
    uint8_t I[0];

    size_t Y_len = hexMessage.length() / 2;
    uint8_t Y[Y_len];
    decode(Y, Y_len, hexMessage.c_str());

    uint8_t X[Y_len];

    size_t T_len = 16;
    uint8_t T[T_len];
    decode(T, T_len, hexMac.c_str());

    bool error = false;
    authDecrypt(l, d, A, countString.length(), K, 32, I, 0, Y, Y_len, X, T, error);
    if (!error) {
      return encode(X, Y_len);
    }
  }
  return ERROR_MESSAGE;
}

void handleKey() {
  ENCRYPTION_KEY.clear();
  ENCRYPTION_KEY = decrypt(INITIAL_ENCRYPTION_KEY);
  if (ENCRYPTION_KEY != ERROR_MESSAGE) {
    Serial.println("encryption key:");
    Serial.println(ENCRYPTION_KEY.c_str());
    ++messageCount;
    server.send(200);
  }
}

void handleBody() {
  if (ENCRYPTIN_ENABLED) {
    string state = decrypt(ENCRYPTION_KEY);
    if (state != ERROR_MESSAGE) {
      if (state == ENCODED_ON_COMMAND) {
          Serial.println("encrypted command received: onn");
          digitalWrite(LED, HIGH);
          ++messageCount;
          server.send(200);
        }
        else if (state == ENCODED_OFF_COMMAND) {
          Serial.println("encrypted command received: off");
          digitalWrite(LED, LOW);
          ++messageCount;
          server.send(200);
        }
    }
  }
  else {
    if (server.hasArg(PARAM_INPUT_1)) {
      String state = server.arg(PARAM_INPUT_1);
      if (state == "onn") {
        Serial.println("command received: onn");
        digitalWrite(LED, HIGH);
        ++messageCount;
        server.send(200);
      }
      else if (state == "off") {
        Serial.println("command received: off");
        digitalWrite(LED, LOW);
        ++messageCount;
        server.send(200);
      }
    }
  }
}

void setup() {
  Serial.begin(115200);
  WiFiManager wifiManager;
  // wifiManager.resetSettings();

  // wifiManager.startConfigPortal(LIGHT_SSID, LIGHT_PASSWORD);
  wifiManager.autoConnect(LIGHT_SSID, LIGHT_PASSWORD);

  pinMode(LED, OUTPUT);
  digitalWrite(LED, HIGH);
  messageCount = 0;

  server.on("/health", HTTP_GET, healthCheck); // health check request
  server.on("/key", HTTP_POST, handleKey); // encryption key
  server.on("/led", HTTP_POST, handleBody); // led commands
  server.begin();
}

void loop() {
  server.handleClient();
}
