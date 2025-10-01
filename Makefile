.PHONY: help up down logs run clean build test

# Default target
help:
	@echo "Available commands:"
	@echo "  up      - Start all services with Docker Compose"
	@echo "  down    - Stop all services"
	@echo "  logs    - Show logs from all services"
	@echo "  run     - Run application locally with dev profile"
	@echo "  build   - Build the application with Maven"
	@echo "  test    - Run tests"
	@echo "  clean   - Clean Maven build artifacts"

# Start all services
up:
	docker compose up -d --build

# Stop all services
down:
	docker compose down

# Show logs
logs:
	docker compose logs -f

# Run application locally with dev profile
run:
	mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build application
build:
	mvn clean compile

# Run tests
test:
	mvn test

# Clean build artifacts
clean:
	mvn clean
	docker compose down -v

