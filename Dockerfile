# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml trước để cache dependencies (nếu mạng ổn)
COPY pom.xml .

# ⚠️ BỎ QUA bước go-offline để tránh lỗi mạng
# RUN mvn dependency:go-offline -B

# Copy toàn bộ source code
COPY src ./src

# Build ứng dụng (bỏ qua test)
RUN mvn clean package -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Thiết lập profile docker (tùy chọn)
ENV SPRING_PROFILES_ACTIVE=docker

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
