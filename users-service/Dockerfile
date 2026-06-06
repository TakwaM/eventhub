FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN apk add --no-cache netcat-openbsd

COPY target/users-service-0.0.1-SNAPSHOT.jar /app/app.jar
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8080

CMD ["/wait-for.sh","postgres","--","java","-jar","/app/app.jar"]
