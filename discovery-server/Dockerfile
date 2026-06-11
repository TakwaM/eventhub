FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer wget pour tests/healthchecks
RUN apk add --no-cache wget

COPY target/discovery-server-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-config.sh /app/wait-for-config.sh
RUN chmod +x /app/wait-for-config.sh

EXPOSE 8761

ENTRYPOINT ["/app/wait-for-config.sh"]