FROM openjdk:22

WORKDIR /app

COPY target/MoySkladTask-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
