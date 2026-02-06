# Multi-stage build para optimizar tamaño de imagen
FROM gradle:8.5-jdk21 AS builder

WORKDIR /build

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle gradle.properties lombok.config main.gradle ./
COPY gradle ./gradle

# Copiar código fuente
COPY domain ./domain
COPY infrastructure ./infrastructure
COPY applications ./applications
COPY deployment ./deployment

# Compilar aplicación (sin tests para build más rápido)
RUN gradle :applications:app-service:build -x test --no-daemon

# Imagen final - solo runtime
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="bancolombia"
LABEL description="Franchise Management System - Reactive API"
LABEL version="1.0.0"

VOLUME /tmp

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Crear directorio de la aplicación
RUN mkdir -p /app && chown -R appuser:appgroup /app

WORKDIR /app

# Copiar JAR desde el builder
COPY --from=builder --chown=appuser:appgroup /build/applications/app-service/build/libs/*.jar app.jar

# Configuración de JVM optimizada para contenedores
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=prod"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Cambiar a usuario no-root
USER appuser

# Puerto de la aplicación
EXPOSE 8080

# Entrypoint optimizado
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

