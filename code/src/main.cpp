#include <Arduino.h>
#include <ESP8266WiFi.h>
#include "ESPAsyncWebServer.h"
#include <secrets.h>
#include "standard77.hpp"
#include <base64.h>

int LED = LED_BUILTIN; // D1
AsyncWebServer server(80);

const char* PARAM_INPUT_1 = "message";
const char* PARAM_INPUT_2 = "mac";

void setup() {
  Serial.begin(115200);
  pinMode(LED, OUTPUT);
  digitalWrite(LED, LOW);

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("Connected");
  Serial.println(WiFi.localIP());

  server.on("/led", HTTP_POST, [](AsyncWebServerRequest *request){
    string message;
    string mac;
    if (request->hasParam(PARAM_INPUT_1, true) && request->hasParam(PARAM_INPUT_2, true)) {
      String encodedMessage = request->getParam(PARAM_INPUT_1, true)->value();
      String encodedMac = request->getParam(PARAM_INPUT_2, true)->value();
      
      // Serial.println("encoded params");
      // Serial.println(encodedMessage);
      // Serial.println(encodedMac);

      message = Base64::decode(encodedMessage.c_str());
      mac = Base64::decode(encodedMac.c_str());

      // Serial.println("params");
      // Serial.println(message.c_str());
      // Serial.println(mac.c_str());

      size_t l = 256;
      size_t d = 1;
      
      uint8_t A[16];
      reverseAndDecode(A, "B194BAC80A08F53B366D008E584A5DE4");
      uint8_t K[32];
      reverseAndDecode(K, "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
      uint8_t I[49];
      reverseAndDecode(I, "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4FF33C657B637C306ADD4EA7799EB23D313E");

      size_t Y_len = message.length();
      uint8_t Y[Y_len];
      for (int i = 0; i < Y_len; ++i) {
          Y[i] = (uint8_t) message[i];
      }

      uint8_t X[Y_len];

      size_t T_len = mac.length();
      uint8_t T[T_len];
      for (int i = 0; i < T_len; ++i) {
          T[i] = (uint8_t) mac[i];
      }

      bool error = false;
      authDecrypt(l, d, A, 16, K, 32, I, 49, Y, Y_len, X, T, error);
      if (!error) {
        string xHexString = encode(X, Y_len);

        Serial.println(xHexString.c_str());

        Serial.println(Y_len);
        Serial.println(T_len);
        for (int i = 0; i < Y_len; ++i) {
          Serial.println(Y[i]);
        }
        Serial.println("---");
        for (int i = 0; i < T_len; ++i) {
          Serial.println(T[i]);
        }
        Serial.println("---");
        for (int i = 0; i < Y_len; ++i) {
          Serial.println(X[i]);
        }

        if (xHexString == "6F6E6E") {
          Serial.println("onn command received");
          digitalWrite(LED, LOW);
        }
        else if (xHexString == "6F6666") {
          Serial.println("off command received");
          digitalWrite(LED, HIGH);
        }
      }
    }
    request->send(200);
  });

  server.begin();
}

void loop() {
}
