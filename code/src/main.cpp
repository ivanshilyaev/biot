#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <secrets.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <WebSerial.h>

int LED = D1;
AsyncWebServer server(80);

/* Message callback of WebSerial */
void recieveMessage(uint8_t *data, size_t len){
  String d = "";
  for(int i=0; i < len; i++){
    d += char(data[i]);
  }
  WebSerial.println(d);
  if (d == "on") {
    digitalWrite(LED, HIGH);
  }
  if (d == "off") {
    digitalWrite(LED, LOW);
  }
}

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

  WebSerial.begin(&server);
  WebSerial.msgCallback(recieveMessage);
  server.begin();
}

void loop() {
}
