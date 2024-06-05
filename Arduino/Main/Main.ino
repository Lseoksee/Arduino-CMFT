#include <Adafruit_OV7670.h>
#include <SPIBrute.h>
#include <image_ops.h>
#include <ov7670.h>

#include <Servo.h>

Servo servo;

// OV7670_arch arch = {.timer = TCC1, .xclk_pdec = false};
// OV7670_pins pins = {.enable = PIN_PCC_D8, .reset = PIN_PCC_D9,
//                     .xclk = PIN_PCC_XCLK};
// Adafruit_OV7670 cam(OV7670_ADDR, &pins, &Wire1, &arch);

void setup() {
  servo.attach(A0);
}

int angle = 0;
void loop() {
  while (angle <= 180) {
    // servo.write(angle);
    angle++;
    delay(10);
  }

  while (angle >= 0) {
    // servo.write(angle);
    angle--;
    delay(10);
  }
}