FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer netcat pour le wait-for
RUN apk add --no-cache netcat-openbsd

# copier l'artefact (ajuste le nom si nécessaire)
COPY target/notifications-service-0.0.1-SNAPSHOT.jar /app/app.jar

# copier le script wait-for
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8085

# utiliser le script ; le script gère host, host:port ou host port
CMD ["/wait-for.sh", "postgres", "--", "java", "-jar", "/app/app.jar"]