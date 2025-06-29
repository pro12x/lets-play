# Étape 1 : Build avec JDK
FROM maven:3.8.6-jdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Étape 2 : Exécution avec JRE léger
FROM eclipse-temurin:17-jre-focal

# Créer un utilisateur non root pour la sécurité
RUN adduser --disabled-login appuser
USER appuser
WORKDIR /home/appuser/app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port HTTP standard de Spring Boot
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]