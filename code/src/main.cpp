#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
#include "standard77.hpp"
#include "base64.hpp"
#include "secrets.h"

ESP8266WebServer server(80);

int LED = LED_BUILTIN; // D1

const char* PARAM_INPUT_1 = "message";
const char* PARAM_INPUT_2 = "mac";

void healthCheck() {
  server.send(200, "text/plain", "Ok");
}

void handleBody() {
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
    
    uint8_t A[16] = {0};
    // reverseAndDecode(A, "B194BAC80A08F53B366D008E584A5DE4");
    uint8_t K[32];
    reverseAndDecode(K, "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
    uint8_t I[49] = {0};
    // reverseAndDecode(I, "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4FF33C657B637C306ADD4EA7799EB23D313E");

    size_t Y_len = hexMessage.length() / 2;
    uint8_t Y[Y_len];
    decode(Y, Y_len, hexMessage.c_str());

    uint8_t X[Y_len];

    size_t T_len = hexMac.length() / 2;
    uint8_t T[T_len];
    decode(T, T_len, hexMac.c_str());

    bool error = false;
    authDecrypt(l, d, A, 16, K, 32, I, 49, Y, Y_len, X, T, error);
    if (!error) {
      string xHexString = encode(X, Y_len);

      if (xHexString == "6f6e6e") {
        Serial.println("onn command received");
        digitalWrite(LED, LOW);
      }
      else if (xHexString == "6f6666") {
        Serial.println("off command received");
        digitalWrite(LED, HIGH);
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
  digitalWrite(LED, LOW);


  server.on("/health", HTTP_GET, healthCheck); // health check request
  server.on("/led", HTTP_POST, handleBody);
  server.begin();
}

void loop() {
  server.handleClient();
}
