FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer netcat pour que wait-for.sh puisse tester le port
RUN apk add --no-cache netcat-openbsd

# copier le jar de l'application (adapte le pattern si nécessaire)
COPY target/users-service-0.0.1-SNAPSHOT.jar /app/app.jar

# copier le script d'attente et le rendre exécutable
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8080

# attendre postgres:5432 puis lancer l'application
CMD ["/wait-for.sh","postgres:5432","--","java","-jar","/app/app.jar"]
