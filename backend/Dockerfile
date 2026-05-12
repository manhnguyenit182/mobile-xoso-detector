# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Bắt buộc: CrawlScheduler cron dùng timezone này
ENV TZ=Asia/Ho_Chi_Minh
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Tối ưu JVM cho Render free tier (512MB RAM)
ENTRYPOINT ["java", \
  "-Xms128m", "-Xmx384m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
