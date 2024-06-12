#include <Servo.h>
#include <math.h>

#define TRIG 12
#define ECHO 13
#define SERVO A0
#define SCAN_SPEED 20   // 서보모터 속도
#define SCAN_TIME 1000  // 스캔 타이밍

Servo servo;
long scanList[181] = { 0 };

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

    if (abs(prev - duration) > 100 && (millis() - detectTime) > SCAN_TIME) {
      notification();
    }

    delay((SCAN_SPEED + duration) - duration);
  }


  while (angle > 0) {
    angle--;
    servo.write(angle);

    long duration = getDuration();
    long prev = scanList[angle];

    if (abs(prev - duration) > 100 && (millis() - detectTime) > SCAN_TIME) {
      notification();
    }

    delay((SCAN_SPEED + duration) - duration);
  }
}

void notification() {
  //TODO: 이제 감지되면 알림 보내게 설정해야함
  Serial.println("이상치 감지됨");
  detectTime = millis();
}

// 초음파 시간 구하기
long getDuration() {
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  return pulseIn(ECHO, HIGH);
}