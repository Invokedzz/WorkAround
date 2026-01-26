FROM eclipse-temurin:21
LABEL authors="invokedzz"
WORKDIR /app
COPY target/WorkAround-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]