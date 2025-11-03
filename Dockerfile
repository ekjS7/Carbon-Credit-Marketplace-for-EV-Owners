# ===============================
# Stage 1: Build ứng dụng với Maven
# ===============================
FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml trước để cache dependencies
COPY pom.xml .


# Tải dependencies để cache (build nhanh hơn các lần sau)
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code vào container
COPY . .

# Build ứng dụng, bỏ qua test để build nhanh hơn
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Runtime - Chạy ứng dụng Spring Boot
# ===============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file JAR từ stage build sang stage runtime
COPY --from=builder /app/target/*.jar app.jar

# Cấu hình cổng ứng dụng
EXPOSE 8080

# Thiết lập biến môi trường tùy chọn
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
