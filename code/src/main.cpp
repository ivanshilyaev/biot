#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
#include "standard77.hpp"
#include "base64.hpp"
#include "secrets.h"

ESP8266WebServer server(80);

int LED = D1;

const char* PARAM_INPUT_1 = "param1";
const char* PARAM_INPUT_2 = "param2";

string ENCRYPTION_KEY;

void healthCheck() {
  server.send(200, "text/plain", "Ok");
}

void handleKey() {
  string hexMessage;
    string hexMac;
    if (server.hasArg(PARAM_INPUT_1) && server.hasArg(PARAM_INPUT_2)) {
      String encodedMessage = server.arg(PARAM_INPUT_1);
      String encodedMac = server.arg(PARAM_INPUT_2);

      uint8_t hexMessageArray[BASE64::decodeLength(encodedMessage.c_str())];
      BASE64::decode(encodedMessage.c_str(), hexMessageArray);
      hexMessage = reinterpret_cast<char *>(hexMessageArray);

      uint8_t hexMacArray[BASE64::decodeLength(encodedMessage.c_str())];
      BASE64::decode(encodedMac.c_str(), hexMacArray);
      hexMac = reinterpret_cast<char *>(hexMacArray); 

      size_t l = 256;
      size_t d = 1;
      
      uint8_t A[0];
      uint8_t K[32];
      reverseAndDecode(K, INITIAL_ENCRYPTION_KEY);
      uint8_t I[0];

      size_t Y_len = hexMessage.length() / 2;
      uint8_t Y[Y_len];
      decode(Y, Y_len, hexMessage.c_str());

      uint8_t X[Y_len];

      size_t T_len = hexMac.length() / 2;
      uint8_t T[T_len];
      decode(T, T_len, hexMac.c_str());

      bool error = false;
      authDecrypt(l, d, A, 0, K, 32, I, 0, Y, Y_len, X, T, error);
      if (!error) {
        ENCRYPTION_KEY = encode(X, Y_len);

        Serial.println("encryption key:");
        Serial.println(ENCRYPTION_KEY.c_str());
      }
    }

  server.send(200);
}

void handleBody() {
  if (ENCRYPTIN_ENABLED) {
    string hexMessage;
    string hexMac;
    if (server.hasArg(PARAM_INPUT_1) && server.hasArg(PARAM_INPUT_2)) {
      String encodedMessage = server.arg(PARAM_INPUT_1);
      String encodedMac = server.arg(PARAM_INPUT_2);

      uint8_t hexMessageArray[BASE64::decodeLength(encodedMessage.c_str())];
      BASE64::decode(encodedMessage.c_str(), hexMessageArray);
      hexMessage = reinterpret_cast<char *>(hexMessageArray);

      uint8_t hexMacArray[BASE64::decodeLength(encodedMessage.c_str())];
      BASE64::decode(encodedMac.c_str(), hexMacArray);
      hexMac = reinterpret_cast<char *>(hexMacArray); 

      size_t l = 256;
      size_t d = 1;
      
      uint8_t A[0];
      uint8_t K[32];
      reverseAndDecode(K, INITIAL_ENCRYPTION_KEY);
      uint8_t I[0];

      size_t Y_len = hexMessage.length() / 2;
      uint8_t Y[Y_len];
      decode(Y, Y_len, hexMessage.c_str());

      uint8_t X[Y_len];

      size_t T_len = hexMac.length() / 2;
      uint8_t T[T_len];
      decode(T, T_len, hexMac.c_str());

      bool error = false;
      authDecrypt(l, d, A, 0, K, 32, I, 0, Y, Y_len, X, T, error);
      if (!error) {
        string xHexString = encode(X, Y_len);

        if (xHexString == "6f6e6e") {
          Serial.println("encrypted command received: onn");
          digitalWrite(LED, HIGH);
        }
        else if (xHexString == "6f6666") {
          Serial.println("encrypted command received: off");
          digitalWrite(LED, LOW);
        }
      }
    }
  }
  else {
    if (server.hasArg(PARAM_INPUT_1)) {
      String state = server.arg(PARAM_INPUT_1);
      if (state == "onn") {
        Serial.println("command received: onn");
        digitalWrite(LED, HIGH);
      }
      else if (state == "off") {
        Serial.println("command received: off");
        digitalWrite(LED, LOW);
      }
    }
  }
  
  server.send(200);
}

void setup() {
  Serial.begin(115200);
  WiFiManager wifiManager;
  // wifiManager.resetSettings();

  // wifiManager.startConfigPortal(LIGHT_SSID, LIGHT_PASSWORD);
  wifiManager.autoConnect(LIGHT_SSID, LIGHT_PASSWORD);

  pinMode(LED, OUTPUT);
  digitalWrite(LED, HIGH);

  server.on("/health", HTTP_GET, healthCheck); // health check request
  server.on("/key", HTTP_POST, handleKey); // encryption key
  server.on("/led", HTTP_POST, handleBody); // led commands
  server.begin();
}

void loop() {
  server.handleClient();
}
