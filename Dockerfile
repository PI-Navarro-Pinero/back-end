FROM maven:3.8.1-jdk-11-slim AS build
COPY src /home/pinp/src
COPY pom.xml /home/pinp
RUN mvn -f /home/pinp/pom.xml clean package

FROM openjdk:11-jre-slim
WORKDIR /pinp
COPY --from=build /home/pinp/target/pinp-1.0.0.jar backend.jar
COPY weapons.example.yaml weapons.yaml
EXPOSE 8080
ENTRYPOINT ["java","-jar","backend.jar"]
