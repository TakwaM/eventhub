FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer netcat et wget pour wait-for.sh
RUN apk add --no-cache netcat-openbsd wget

# copier le jar
COPY target/reservations-service-0.0.1-SNAPSHOT.jar /app/app.jar

# copier le script d'attente et forcer conversion + permission
COPY wait-for.sh /wait-for.sh
RUN sed -i 's/\r$//' /wait-for.sh \
 && awk 'NR==1{sub(/^\xef\xbb\xbf/,"")}{print}' /wait-for.sh > /wait-for.tmp \
 && mv /wait-for.tmp /wait-for.sh \
 && chmod 755 /wait-for.sh

EXPOSE 8082

# invoquer sh explicitement pour éviter les problèmes de shebang
CMD ["sh","/wait-for.sh","postgres","5432","--","java","-jar","/app/app.jar"]