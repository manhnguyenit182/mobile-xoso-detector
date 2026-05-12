# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q
COPY src ./src
RUN ./mvnw package -DskipTests -q

# Stage 2: Runtime — tối ưu memory cho Render free tier (512 MB RAM)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV TZ=Asia/Ho_Chi_Minh
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Giới hạn heap để tránh OOM trên Render free 512MB:
# -Xms64m  : Start heap nhỏ, tránh chiếm RAM ngay khi khởi động
# -Xmx320m : Max heap 320MB → tổng JVM process ~420MB < 512MB limit
# -XX:+UseSerialGC    : GC nhẹ, phù hợp single-core free tier
# -XX:TieredStopAtLevel=1 : Giảm JIT compilation → startup nhanh hơn ~30%
ENTRYPOINT ["java", \
  "-Xms64m", "-Xmx320m", \
  "-XX:+UseSerialGC", \
  "-XX:TieredStopAtLevel=1", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
