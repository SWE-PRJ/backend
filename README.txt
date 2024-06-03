# SWE PRJ 6조 Issue Tracker Backend

2024-1학기 소프트웨어공학 프로젝트 6조 Issue Tracker의 백엔드 어플리케이션입니다.

## 개발 환경
- Spring Boot 3.2.5
- JDK 17
- MySQL 8.0
- Intellij IDEA
- Windows / macOS / Ubuntu

## 요구 환경
- JDK 17
- MySQL 8.0
- Intellij IDEA 혹은 기타 Java IDE

## 실행 방법

### IDE에서 실행

1. git clone
   $ git clone https://github.com/SWE-PRJ/backend.git

2. Intellij IDEA 등의 IDE에서 해당 프로젝트를 열고 실행

### 빌드 파일 실행

1. MySQL 계정 생성(계정이 없는 경우)
   다음 계정으로 MySQL 계정을 생성한다.
   username: root 
   password: sqlpassword

2. MySQL 계정이 이미 있는 경우
   Windows (cmd):
   set SPRING_DATASOURCE_USERNAME={ your_username }
   set SPRING_DATASOURCE_PASSWORD={ your_password }

   Windows (PowerShell):
   $ env:SPRING_DATASOURCE_USERNAME = "{ your_username }"
   $ env:SPRING_DATASOURCE_PASSWORD = "{ your_password }"

   macOS / Linux:
   export SPRING_DATASOURCE_USERNAME={ your_username }
   export SPRING_DATASOURCE_PASSWORD={ your_password }

3. jar 파일 실행
   $ cd build/libs
   $ java -jar {jar파일명}.jar
