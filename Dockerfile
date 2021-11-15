FROM openjdk:16-alpine
COPY plugins plugins/
COPY controller controller/
COPY backend/build/libs/backend-1.0-SNAPSHOT.jar backend.jar
COPY config.xml config.xml

EXPOSE 1337

CMD ["java", "-jar", "backend.jar"]