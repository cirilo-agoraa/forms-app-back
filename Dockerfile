FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew build --no-daemon --parallel

COPY src ./src

CMD ["./gradlew", "bootRun"]