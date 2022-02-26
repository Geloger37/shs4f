#include "DHT.h"
#define DHTPIN 2 // Дата пин для датчика DHT11
#define INIT_MESSAGE "initial arduino" // Сообщение включения arduino
#define READY_MESSAGE 200
#define TEST_DATABITS_MESSAGE 255

DHT dht(DHTPIN, DHT11); 
bool readyToSend = false; // Готовность arduino к отправке значений с датчика
int temp;

void setup() {
  dht.begin();
  Serial.begin(2400);
  Serial.print(INIT_MESSAGE); // Отправка сообщения включения arduino
}

void loop() {
  if (readyToSend){ // Состояние передачи данных с датчика
    temp = dht.readTemperature();
    Serial.print(temp);
    delay(1000);
  }else{ // Состояние инициализации 
    if (Serial.available() > 0) {
      int incomingByte = Serial.read();
      if(incomingByte == TEST_DATABITS_MESSAGE){ // Тестирование databits
        Serial.write(0b11111111);
      }
      if(incomingByte == READY_MESSAGE){ // Переход в состояние готовкности отправки значений с датичка
        readyToSend = true;
      }
    }
    delay(50);
  }
}
