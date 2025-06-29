# Dockerfile multi-stage pour Spring Boot avec Java 17
FROM eclipse-temurin:17-jdk-alpine AS builder

# Installation de Maven
RUN apk add --no-cache maven

# Définition du répertoire de travail
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Téléchargement des dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# Copie du code source
COPY src ./src

# Construction de l'application
RUN mvn clean package -DskipTests

# Stage de production
FROM eclipse-temurin:17-jre-alpine

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser

# Répertoire de travail
WORKDIR /app

# Copie du JAR depuis le stage de build
COPY --from=builder /app/target/*.jar app.jar

# Changement de propriétaire des fichiers
RUN chown -R appuser:appuser /app

# Passage à l'utilisateur non-root
USER appuser

# Variables d'environnement pour Render
ENV PORT=${SERVER_PORT:-1111}
ENV SPRING_PROFILES_ACTIVE=prod

# Exposition du port
EXPOSE ${SERVER_PORT:-1111}

# Point d'entrée
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "app.jar"]