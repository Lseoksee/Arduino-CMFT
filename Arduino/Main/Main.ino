#include <Servo.h>
#include <math.h>

#define TRIG A4
#define ECHO A5
#define SERVO A3
#define SCAN_SPEED 20   // 서보모터 속도
#define SCAN_TIME 1000  // 스캔 타이밍

void setup() {
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  Serial.begin(9600);
  servo.attach(SERVO);
}

long detectTime = 0;
void loop() {
  int angle = -1;

  while (angle < 180) {
    angle++;
    servo.write(angle);

    long duration = getDuration();
    long prev = scanList[angle];

    if (prev == 0) {
      scanList[angle] = duration;
      prev = duration;
    }

    Serial.println(abs(prev - duration));

    if (abs(prev - duration) > 1000 && (millis() - detectTime) > SCAN_TIME) {
      notification();
    }

    delay((SCAN_SPEED + duration) - duration);
  }


  while (angle > 0) {
    angle--;
    servo.write(angle);

    long duration = getDuration();
    long prev = scanList[angle];

    if (abs(prev - duration) > 1000 && (millis() - detectTime) > SCAN_TIME) {
      notification();
    }

    Serial.println(abs(prev - duration));

    delay((SCAN_SPEED + duration) - duration);
  }
}

// 초음파 시간 구하기
long getDuration() {
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  return pulseIn(ECHO, HIGH);
}