#include <Arduino.h>

void setup() {
  Serial.begin(115200);
  pinMode(D1, OUTPUT);
}

void loop() {
  digitalWrite(D1, LOW);
  delay(1000);
  digitalWrite(D1, HIGH);
  delay(1000);
}