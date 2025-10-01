# Spring Boot Demo Project

Dự án Spring Boot demo với MySQL, chạy trên Docker Compose.

## Công nghệ sử dụng

- **Java 21**
- **Spring Boot 3.2.0**
- **Maven**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Flyway** (Database Migration)
- **Lombok**

## Dependencies chính

- Spring Web
- Spring Data JPA
- Validation
- MySQL Driver
- Flyway Core & MySQL
- Lombok

## Cấu trúc project

```
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── entity/User.java
│   │   │   ├── repository/UserRepository.java
│   │   │   ├── controller/UserController.java
│   │   │   └── DemoApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-docker.yml
│   │       └── db/migration/V1__init.sql
├── Dockerfile
├── docker-compose.yml
├── Makefile
└── pom.xml
```

## Cách chạy project

### 1. Chạy với Docker Compose (Recommended)

```bash
# Build và chạy tất cả services
docker compose up -d --build

# Hoặc sử dụng Makefile
make up
```

Services sẽ chạy trên:
- **Application**: http://localhost:8080
- **MySQL**: localhost:3306
- **Adminer** (Database UI): http://localhost:8081

### 2. Chạy local development

Trước tiên, chạy MySQL trong Docker:
```bash
docker compose up -d db
```

Sau đó chạy application với profile `dev`:
```bash
# Sử dụng Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Hoặc sử dụng Makefile
make run
```

## API Endpoints

### GET /api/users
Lấy danh sách tất cả users

```bash
curl -X GET http://localhost:8080/api/users
```

### POST /api/users
Tạo user mới

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "fullName": "John Doe"
  }'
```

## Makefile Commands

```bash
# Chạy tất cả services
make up

# Dừng tất cả services
make down

# Xem logs
make logs

# Chạy app local với profile dev
make run

# Xem tất cả commands
make help
```

## Database

### Connection Info
- **Host**: localhost (dev) / db (docker)
- **Port**: 3306
- **Database**: appdb
- **Username**: appuser
- **Password**: secret
- **Root Password**: rootsecret

### Adminer Access
Truy cập Adminer tại: http://localhost:8081
- **System**: MySQL
- **Server**: db
- **Username**: appuser
- **Password**: secret
- **Database**: appdb

## Spring Profiles

- **dev**: Kết nối MySQL trên localhost (cho development)
- **docker**: Kết nối MySQL host = "db" (cho Docker environment)

## Database Migration

Flyway sẽ tự động chạy migration scripts trong `src/main/resources/db/migration/` khi application khởi động.

## Troubleshooting

### Lỗi kết nối database
```bash
# Kiểm tra MySQL container
docker compose logs db

# Restart services
docker compose restart
```

### Xem logs application
```bash
# Logs tất cả services
docker compose logs

# Logs chỉ app
docker compose logs app

# Follow logs real-time
docker compose logs -f app
```

### Reset database
```bash
# Xóa volume và restart
docker compose down -v
docker compose up -d --build
```

