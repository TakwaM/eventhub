FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# installer wget pour tests/healthchecks
RUN apk add --no-cache wget

# copier l'artefact et le script
COPY target/discovery-server-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-config.sh /app/wait-for-config.sh

# convertir CRLF -> LF, supprimer BOM si présent, rendre exécutable
RUN sed -i 's/\r$//' /app/wait-for-config.sh \
 && awk 'NR==1{sub(/^\xef\xbb\xbf/,"")}{print}' /app/wait-for-config.sh > /app/wait-for-config.tmp \
 && mv /app/wait-for-config.tmp /app/wait-for-config.sh \
 && chmod 755 /app/wait-for-config.sh

EXPOSE 8761

# lancer explicitement via sh pour éviter les problèmes de shebang corrompu
ENTRYPOINT ["sh", "/app/wait-for-config.sh"]