#include <Arduino.h>

byte lastButtonState;
byte ledState = LOW;

unsigned long lastTimeButtonStateChanged = millis();
unsigned long debounceDuration = 50; // millis

// D1 = LED_PIN
// D5 = BUTTON_PIN

void setup() {
  pinMode(D1, OUTPUT);
  pinMode(D5, INPUT_PULLUP);
  lastButtonState = digitalRead(D5);
}

void loop() {
  if (millis() - lastTimeButtonStateChanged >= debounceDuration) {
    byte buttonState = digitalRead(D5);
    if (buttonState != lastButtonState) {
      lastTimeButtonStateChanged = millis();
      lastButtonState = buttonState;
      if (buttonState == LOW) {
        if (ledState == HIGH) {
          ledState = LOW;
        }
        else {
          ledState = HIGH;
        }
        digitalWrite(D1, ledState);
      }
    }
  }
}
