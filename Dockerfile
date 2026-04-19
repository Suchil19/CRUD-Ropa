# Etapa 1: Construcción (Builder)
# Usamos el JDK 17 de Eclipse Temurin para compilar el proyecto
FROM eclipse-temurin:17-jdk AS builder

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el "wrapper" de Maven y el archivo pom.xml primero para aprovechar el caché de Docker
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Descargamos las dependencias (esto ahorra tiempo en futuras construcciones)
RUN ./mvnw dependency:go-offline

# Copiamos el código fuente de tu aplicación
COPY src ./src

# Compilamos y generamos el archivo JAR (saltando los tests para ir más rápido)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
# Usamos el JRE 17 (más ligero que el JDK) para correr la app
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos solo el archivo JAR generado en la etapa anterior
# Buscamos el nombre del artifactId y versión definido en tu pom: demo-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto estándar de Spring Boot
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]