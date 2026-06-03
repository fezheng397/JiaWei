FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN addgroup --system recipes && adduser --system --ingroup recipes recipes
COPY --from=build /workspace/target/recipes-api-*.jar app.jar
USER recipes

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
