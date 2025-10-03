# Recipe Manager 🥗

Aplicatie Spring Boot pentru gestionarea rețetelor și ingredientelor, cu persistență în PostgreSQL și monitorizare prin Prometheus.

## ✅ Funcționalități

- CRUD pentru Rețete
- CRUD pentru Ingrediente
- Relație many-to-many: rețetă - ingrediente (+ quantity)
- Validări + Mesaje de eroare custom
- Monitorizare metrici via Actuator & Prometheus
- Docker + Docker Compose ready

## 🚀 Tehnologii

- Java 21, Spring Boot 3
- Maven
- PostgreSQL
- Flyway
- Micrometer + Prometheus
- Docker

## ▶️ Rulare locală

```bash
# Rulează Postgres + Prometheus
docker compose up -d

# Build + Run aplicația
mvn clean package -DskipTests
docker build -t recipe-manager .
docker run --rm -p 8081:8081 --network recipe-manager_default recipe-manager
