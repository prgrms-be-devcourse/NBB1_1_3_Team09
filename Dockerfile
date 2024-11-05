FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/app.jar app.jar
COPY src/main/resources/application.yml /app/config/application.yml
RUN mkdir /logs && chmod 755 /logs
VOLUME /logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=file:/app/config/application.yml"]
