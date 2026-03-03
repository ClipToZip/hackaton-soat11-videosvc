# Build JAR
FROM maven:3.9.9-amazoncorretto-17 AS build
COPY src /tmp/src/
COPY pom.xml /tmp/
WORKDIR /tmp/
RUN mvn clean install

# Execute JAR
FROM amazoncorretto:17.0.14
COPY --from=build /tmp/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]