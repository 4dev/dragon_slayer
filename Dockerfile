FROM openjdk:8-jre-alpine

WORKDIR /app
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]