# Multi-stage build para optimizar tamaño de imagen
FROM gradle:8.14-jdk21-alpine AS builder

WORKDIR /build

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle gradle.properties lombok.config main.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Copiar código fuente
COPY domain ./domain
COPY infrastructure ./infrastructure
COPY applications ./applications

# Compilar aplicación (sin tests y sin validación de estructura para build más rápido)
RUN gradle :app-service:build -x test -x validateStructure --no-daemon

# ============================================
# Imagen final - solo runtime (OpenJ9 para soportar scaffold Bancolombia)
# ============================================
FROM ibm-semeru-runtimes:open-21-jre

LABEL maintainer="bancolombia"
LABEL description="Franchise Management System - Reactive API"
LABEL version="1.0.0"

VOLUME /tmp

# Crear usuario no-root para seguridad (comandos para Ubuntu/Debian)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copiar JAR desde el builder
COPY --from=builder --chown=appuser:appgroup /build/applications/app-service/build/libs/*.jar app.jar

# Configuración de JVM optimizada para contenedores (basado en scaffold Bancolombia con OpenJ9)
ENV JAVA_OPTS="-Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=70 \
    -Djava.security.egd=file:/dev/./urandom"

# Exponer puerto
EXPOSE 8080

# Cambiar a usuario no-root
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Entrypoint similar al scaffold de Bancolombia
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


