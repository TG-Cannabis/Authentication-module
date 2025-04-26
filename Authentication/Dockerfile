# Usa una imagen base de OpenJDK 21
FROM openjdk:21-jdk-slim

# Crea un directorio para la app
WORKDIR /app

# Copia solo el .jar generado
COPY target/authentication-0.0.1-SNAPSHOT.jar /app/app.jar


# Expone el puerto
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
