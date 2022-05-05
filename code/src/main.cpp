#include <Arduino.h>
#include <ESP8266WiFi.h>
#include "ESPAsyncWebServer.h"
#include <secrets.h>
#include <bee2/core/mem.h>
#include <bee2/core/hex.h>
#include <bee2/crypto/bash.h>
#include <bee2/crypto/belt.h>

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
    String message;
    String mac;
    if (request->hasParam(PARAM_INPUT_1) && request->hasParam(PARAM_INPUT_2)) {
      message = request->getParam(PARAM_INPUT_1)->value();
      mac = request->getParam(PARAM_INPUT_2)->value();

      octet buf[192];
      // octet hash[64];
      octet state[1024];
      size_t pos;

      // 1. start
      bashPrgStart(state, 256, 1, beltH(), 16, beltH() + 32, 32);
      // 2.1 absorb
      bashPrgAbsorb(beltH() + 64, 49, state);
      // 2.2 decrypt
      bashPrgDecrStart(state);
      for (pos = 0; pos < 192; pos += 192 / 6)
        bashPrgDecrStep(buf + pos, 192 / 6, state);
      if (!memIsZero(buf, 192))
        Serial.println("Decryption error");
      if (hexEq(buf, "6F6E6E")) {
        Serial.println("onn command received");

        digitalWrite(LED, LOW);
        // 2.3 squeeze
        // bashPrgSqueezeStart(state);
        // bashPrgSqueezeStep(buf, 14, state);
        // bashPrgSqueezeStep(buf + 14, 32 - 14, state);
        // if (!memEq(buf, hash, 32))
        //   Serial.println("Check error");
        // else {
        //   digitalWrite(LED, LOW);
        // }
      }
      else if (hexEq(buf, "6F6666")) {
        Serial.println("off command received");

        digitalWrite(LED, HIGH);
        // 2.3 squeeze
        // bashPrgSqueezeStart(state);
        // bashPrgSqueezeStep(buf, 14, state);
        // bashPrgSqueezeStep(buf + 14, 32 - 14, state);
        // if (!memEq(buf, hash, 32))
        //   Serial.println("Check error");
        // else {
        //   digitalWrite(LED, HIGH);
        // }
      }
    }
    request->send(200);
  });

  server.begin();
}

void loop() {
}
