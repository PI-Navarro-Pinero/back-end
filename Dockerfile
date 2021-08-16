FROM maven:3.8.1-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11-jre-slim
WORKDIR /pinp
COPY --from=build /home/app/target/back-end-0.0.1-SNAPSHOT.jar backend.jar
RUN mkdir -p {weaponry,actions,outputs,.logs}
EXPOSE 8080
ENTRYPOINT ["java","-jar","backend.jar"]