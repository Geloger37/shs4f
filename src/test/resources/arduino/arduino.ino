#include "DHT.h"
#define DHTPIN 2

DHT dht(DHTPIN, DHT11);

const int sensorPinIn = A0;  // Analog input pin that the potentiometer is attached to
bool readyToSend = false;
int temp;

void setup() {
  pinMode(sensorPinIn, INPUT);
  pinMode(LED_BUILTIN, OUTPUT);
  dht.begin();
  Serial.begin(2400);
  Serial.print("initial arduino");
  
}

void loop() {
  if (readyToSend){
    temp = dht.readTemperature();
    Serial.print(temp);
    delay(1000);
  }else{
    if (Serial.available() > 0) {
      int incomingByte = Serial.read();
  
      if(incomingByte == 255){
        Serial.write(0b11111111);
      }
     
      if(incomingByte == 200){
        readyToSend = true;
      }
    }
    delay(50);
  }
}
