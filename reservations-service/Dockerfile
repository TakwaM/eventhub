FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer netcat pour wait-for.sh
RUN apk add --no-cache netcat-openbsd

# copier le jar
COPY target/reservations-service-0.0.1-SNAPSHOT.jar /app/app.jar

# copier le script d'attente
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8082

# attendre Postgres + MongoDB puis lancer Spring Boot
CMD ["/wait-for.sh", "--", "java", "-jar", "/app/app.jar"]
