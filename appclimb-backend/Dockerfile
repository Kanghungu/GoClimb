# 1단계: 빌드
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Gradle Wrapper 먼저 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
RUN chmod +x ./gradlew

# 의존성 먼저 다운로드 (캐시 최적화)
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 (가벼운 이미지)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
