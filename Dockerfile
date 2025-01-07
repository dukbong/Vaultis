# Build stage
FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY . .

# gradlew 파일에 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 수행 (테스트 제외)
RUN ./gradlew clean build -x test

RUN rm -f /app/build/libs/*-plain.jar

# Run stage
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar vaultis.jar

ENTRYPOINT ["java", "-jar", "vaultis.jar"]
