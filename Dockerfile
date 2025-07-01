# Multi-stage build for Spring Boot on Railway
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy đúng tên file JAR, thay đổi nếu tên JAR khác
COPY --from=build /app/target/QuanLyBanDienThoai-0.0.1-SNAPSHOT.jar app.jar
# Railway sẽ truyền biến PORT, Spring Boot cần đọc biến này
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]