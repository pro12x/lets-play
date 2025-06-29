# Étape 1 : Build avec JDK 17 et Maven
FROM eclipse-temurin:17-jdk-jammy AS build

# Installer Maven manuellement
RUN apt-get update && \
    apt-get install -y wget && \
    wget https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz  -O /tmp/maven.tar.gz && \
    mkdir -p /opt/maven && \
    tar -xzf /tmp/maven.tar.gz -C /opt/maven && \
    ln -s /opt/maven/apache-maven-3.8.6 /opt/maven/maven && \
    rm /tmp/maven.tar.gz

ENV MAVEN_HOME=/opt/maven/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

WORKDIR /app
COPY . .
RUN mvn clean package

# Étape 2 : Runtime avec JRE léger
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