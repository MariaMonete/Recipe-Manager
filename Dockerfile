#imagine cu Java 21
FROM eclipse-temurin:21-jdk-jammy

# Setam directorul de lucru în container
WORKDIR /app

# Copiem fișierul jar generat de Spring Boot
COPY target/recipe-manager-0.0.1-SNAPSHOT.jar app.jar

# Expunem portul 8080
EXPOSE 8080

# Comanda care pornește aplicația
ENTRYPOINT ["java", "-jar", "app.jar"]
