#include <Arduino.h>
#include <ESP8266WiFi.h>
#include "ESPAsyncWebServer.h"
#include <secrets.h>

int LED = D1;
AsyncWebServer server(80);

const char* PARAM_INPUT = "state";

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

  server.on("/led", HTTP_GET, [](AsyncWebServerRequest *request){
    String inputMessage;
    if (request->hasParam(PARAM_INPUT)) {
      inputMessage = request->getParam(PARAM_INPUT)->value();
      digitalWrite(LED, inputMessage.toInt());
    }
    request->send(200);
  });

  server.begin();
}

void loop() {
}
