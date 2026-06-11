#include <SPI.h>
#include <MFRC522.h>

#define SS_PIN  10
#define RST_PIN 9

MFRC522 rfid(SS_PIN, RST_PIN);

void setup() {
  Serial.begin(9600);
  SPI.begin();
  rfid.PCD_Init();
  Serial.println("SARS - Lector RFID listo");
}

void loop() {
  if (!rfid.PICC_IsNewCardPresent()) return;
  if (!rfid.PICC_ReadCardSerial())   return;

  String codigo = "";
  for (byte i = 0; i < rfid.uid.size; i++) {
    if (rfid.uid.uidByte[i] < 0x10) codigo += "0";
    codigo += String(rfid.uid.uidByte[i], HEX);
  }
  codigo.toUpperCase();
  Serial.println(codigo);

  rfid.PICC_HaltA();
  rfid.PCD_StopCrypto1();
  delay(1000);
}