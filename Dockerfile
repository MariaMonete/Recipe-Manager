#imagine cu Java 21
FROM eclipse-temurin:21-jdk-jammy

# Setam directorul de lucru în container
WORKDIR /app

# Copiem fișierul jar generat de Spring Boot in imagine
COPY target/recipe-manager-0.0.1-SNAPSHOT.jar app.jar
# copiem dependintele in imagine -> q1`care acum sunt la target/dependency
COPY target/dependency/ ./dependency/

# Expunem portul 8081
EXPOSE 8081

# Comanda care pornește aplicația-> cu classpath manual
ENTRYPOINT ["java","-cp","/app/app.jar:/app/dependency/*","com.maria.recipe_manager.RecipeManagerApplication"]
