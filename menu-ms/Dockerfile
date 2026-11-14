# Etapa 1: compilación (el JAR se construye dentro del contenedor,
# así cualquiera del equipo obtiene la misma imagen sin depender de su Maven local)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2: ejecución con imagen mínima (solo JRE, sin compilador ni código fuente)
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /build/target/app.jar app.jar
# El proceso NO corre como root dentro del contenedor
USER app
EXPOSE 8081
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
