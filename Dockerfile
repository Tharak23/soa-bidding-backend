# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY common/pom.xml common/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY auction-service/pom.xml auction-service/pom.xml
COPY bidding-service/pom.xml bidding-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY . .
ARG MODULE
RUN mvn -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
ARG MODULE
COPY --from=build /src/${MODULE}/target/${MODULE}-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
