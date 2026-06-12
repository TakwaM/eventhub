FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer netcat et wget pour le wait script
RUN apk add --no-cache netcat-openbsd wget

COPY target/events-service-0.0.1-SNAPSHOT.jar /app/app.jar
COPY wait-for.sh /wait-for.sh

# convertir CRLF -> LF, supprimer BOM si présent, rendre exécutable
RUN sed -i 's/\r$//' /wait-for.sh \
 && awk 'NR==1{sub(/^\xef\xbb\xbf/,"")}{print}' /wait-for.sh > /wait-for.tmp \
 && mv /wait-for.tmp /wait-for.sh \
 && chmod 755 /wait-for.sh

EXPOSE 8081

# invoquer sh explicitement pour éviter les problèmes de shebang
CMD ["sh","/wait-for.sh","postgres","--","java","-jar","/app/app.jar"]