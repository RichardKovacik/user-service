# ================================
# Stage 1: Build jar s Mavenom
# ================================
#FROM maven:3.9.6-eclipse-temurin-21 AS build
#
## Nastav pracovný adresár
#WORKDIR /app
#
## Skopíruj Maven súbory (pom.xml + settings)
#COPY pom.xml .
#COPY src ./src
#
## Build projektu s Mavenom (skip testy pre rýchlosť)
#RUN mvn clean package -DskipTests

# ================================
# Stage 2: Runtime image
# ================================
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Skopíruj jar z predošlej stage
#COPY --from=build /app/target/*.jar app.jar
COPY target/*.jar app.jar

# Exponuj port
EXPOSE 8081

# Spustenie aplikácie
ENTRYPOINT ["java","-jar","app.jar"]