FROM maven:3.8.1-jdk-11-slim AS build
COPY src /home/pinp/src
COPY pom.xml /home/pinp
RUN mvn -f /home/pinp/pom.xml clean package

FROM openjdk:11-jre-slim
RUN groupadd -g 5000 pinp \
    && useradd -r -u 5000 -g pinp pinp
USER pinp
WORKDIR /pinp
COPY --from=build /home/pinp/target/back-end-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","backend.jar"]
