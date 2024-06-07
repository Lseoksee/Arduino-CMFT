#include <Servo.h>
#include <math.h>
#define TRIG A1
#define ECHO A2

Servo servo;

int scanList[180] = { NULL };

void setup() {
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  Serial.begin(9600);
  servo.attach(A0);
  servo.write(0);
}

int angle = 0;
void loop() {
  while (angle < 180) {
    // servo.write(angle);
    angle++;

    digitalWrite(TRIG, LOW);
    delayMicroseconds(2);
    // digitalWrite(TRIG, HIGH);
    long duration = pulseIn(ECHO, HIGH);
    scanList[angle - 1] = duration;
  }

  while (angle > 0) {
    angle--;
    // servo.write(angle);

    digitalWrite(TRIG, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIG, HIGH);
    long duration = pulseIn(ECHO, HIGH);
    // 5, 10

    scanList[angle] = duration;
  }
}